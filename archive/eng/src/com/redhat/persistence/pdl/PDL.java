/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.pdl;

import com.arsdigita.util.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.pdl.nodes.*;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * PDL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2004/09/30 $
 **/

public class PDL {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/PDL.java#16 $ by $Author: rhs $, $DateTime: 2004/09/30 15:44:52 $";
    private final static Logger LOG = Logger.getLogger(PDL.class);

    public static final String LINK = "@link";

    private AST m_ast = new AST();
    private boolean m_autoLoad;

    public PDL() {
        this(true);
    }

    private PDL(boolean autoLoad) {
        m_autoLoad = autoLoad;
    }

    public void load(Reader r, String filename) {
        try {
            PDLParser p = new PDLParser(r);
            FileNd file = p.file(filename);
            m_ast.add(AST.FILES, file);
        } catch (ParseException e) {
            throw new WrappedError(filename, e);
        }
    }

    public void loadResource(String s) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(s);
        if (is == null) {
            throw new PDLException("No such resource: " + s);
        }
        load(new InputStreamReader(is), s);
    }

    private void emitGlobal(Root root) {
        PDL glob = new PDL(false);
        glob.loadResource("com/redhat/persistence/pdl/global.pdl");
        glob.emit(root);
    }

    private String linkName(AssociationNd assn) {
	Model m = Model.getInstance(assn.getFile().getModel().getName());
	return m.getQualifiedName() + "." +
	    linkName(assn.getRoleOne(), assn.getRoleTwo());
    }

    private String linkName(PropertyNd one, PropertyNd two) {
	return (m_symbols.lookup(one.getType()) + ":" +
		one.getName().getName() + "::" +
		m_symbols.lookup(two.getType()) + ":" +
		two.getName().getName() +
		"::Link").replace('.', '_');
    }

    private Root m_root;
    private ErrorReport m_errors;
    private SymbolTable m_symbols;
    private HashSet m_links;
    private HashSet m_fks;
    private HashMap m_propertyCollisions;
    private HashMap m_emitted;
    private HashMap m_nodes;

    void emit(Node node, Object emitted) {
        m_nodes.put(emitted, node);
        m_emitted.put(node, emitted);
    }

    Node getNode(Object emitted) {
        return (Node) m_nodes.get(emitted);
    }

    Object getEmitted(Node node) {
        return m_emitted.get(node);
    }

    public void emit(Root root) {
	m_root = root;
        m_errors = new ErrorReport();
        m_symbols = new SymbolTable(m_errors, m_root);
        m_links = new HashSet();
        m_fks = new HashSet();
        m_propertyCollisions = new HashMap();
        m_emitted = new HashMap();
        m_nodes = new HashMap();

        m_ast.traverse(new Node.Switch() {
            public void onNode(Node nd) {
                for (Iterator it = nd.getFields().iterator(); it.hasNext(); ) {
                    String error = nd.validate((Node.Field) it.next());
                    if (error != null) {
                        m_errors.fatal(nd, error);
                    }
                }
            }
        });

        m_errors.check();

        if (m_autoLoad && root.getObjectType("global.String") == null) {
            emitGlobal(root);
        }

        for (Iterator it = m_root.getObjectTypes().iterator();
	     it.hasNext(); ) {
            m_symbols.addEmitted((ObjectType) it.next());
        }

        m_ast.traverse(new Node.Switch() {
            public void onObjectType(ObjectTypeNd ot) {
                if (ot.hasReturns()) {
                    m_errors.warn(ot, "returns clause is deprecated");
                }
                m_symbols.define(ot);
            }
        });

	m_errors.check();

        m_ast.traverse(new Node.Switch() {
            public void onType(TypeNd t) {
                m_symbols.resolve(t);
            }
        });

	m_errors.check();

        m_symbols.sort();

	m_errors.check();

        m_symbols.emit();

        m_errors.check();

	m_ast.traverse(new Node.Switch() {
            public void onAssociation(AssociationNd assn) {
                if (assn.getProperties().size() == 0) {
                    return;
                }

                ObjectType ot = new ObjectType
                    (Model.getInstance
                     (assn.getFile().getModel().getName()),
                     linkName(assn.getRoleOne(), assn.getRoleTwo()), null);
                m_symbols.addEmitted(ot);
            }
        });

        m_ast.traverse(new Node.Switch() {
            private Role define(ObjectType type, PropertyNd prop) {
                String name = prop.getName().getName();

                // Check for collisions
                Property prev = (Property) m_propertyCollisions.get
                    (type.getQualifiedName() + ":" + name);
                if (prev == null) {
                    prev = type.getProperty(name);
                }
                if (prev != null) {
                    m_errors.fatal
                        (prop, "duplicate property: " + name +
                         ", previous definition: " +
                         getNode(prev).getLocation());
                    return null;
                }

                // Check for bad multiplicity
                Integer upper = prop.getUpper();
                Integer lower = prop.getLower();
                if (upper != null && upper.intValue() <= 0
                    || lower != null && lower.intValue() < 0
                    || (upper != null && lower != null
                        && upper.intValue() < lower.intValue())) {
                    m_errors.fatal(prop, "bad multiplicity: " + name);
                    return null;
                }

                TypeReference tref =
                    m_symbols.getTypeReference(prop.getType());
                if (tref == null) {
                    throw new IllegalStateException
                        ("type doesn't have reference: " + prop.getType());
                }
                // Create the property
                Role result =
                    new Role(prop.getName().getName(),
                             tref,
                             prop.isComponent() || prop.getNestedMap() != null,
                             prop.isCollection(),
                             prop.isNullable());
                m_symbols.setLocation(result, prop);

                type.addProperty(result);
                if (prop.isImmediate()) {
                    type.addImmediateProperty(result);
                }

                // Track what node defined this property and vice versa
                emit(prop, result);

                // Track for collision detection
                ObjectType ot = type;
                while (ot != null) {
                    m_propertyCollisions.put
                        (ot.getQualifiedName() + ":" + name, result);
                    ot = ot.getSupertype();
                }

                return result;
            }

            public void onProperty(PropertyNd prop) {
                ObjectType type =
                    m_symbols.getEmitted((ObjectTypeNd) prop.getParent());

                Role role = define(type, prop);

                /* if the property is a composite, the other end is a
                 * needs to be set up for cascading deletes to work
                 */
                if (prop.isComposite()) {
                    String rev = "~" + prop.getName().getName() + ":" +
                        type.getQualifiedName().replace('.', '$');
                    Role reverse = new Role(rev, type, true, true, true);
                    role.getType().addProperty(reverse);
                    role.setReverse(reverse);
                }
            }

            public void onAssociation(AssociationNd assn) {
                PropertyNd one = assn.getRoleOne();
                PropertyNd two = assn.getRoleTwo();
                if (one.isComposite()) { two.setComponent(); }
                if (two.isComposite()) { one.setComponent(); }
                Collection props = assn.getProperties();
                ObjectType oneot =
                    m_symbols.getEmitted(one.getType());
                ObjectType twoot =
                    m_symbols.getEmitted(two.getType());

                if (props.size() > 0) {
                    Role rone = new Role(one.getName().getName(), oneot,
                                         one.isComponent(), false, false);
                    m_symbols.setLocation(rone, one);
                    Role rtwo = new Role(two.getName().getName(), twoot,
                                         two.isComponent(), false, false);
                    m_symbols.setLocation(rtwo, two);

                    ObjectType ot = m_symbols.getEmitted(linkName(assn));

                    ot.addProperty(rone);
                    Role revOne = new Role(rtwo.getName() + LINK, ot,
                                           true,
                                           two.isCollection(),
                                           two.isNullable());
                    rone.getType().addProperty(revOne);
                    rone.setReverse(revOne);

                    ot.addProperty(rtwo);
                    Role revTwo = new Role(rone.getName() + LINK, ot,
                                           true,
                                           one.isCollection(),
                                           one.isNullable());
                    rtwo.getType().addProperty(revTwo);
                    rtwo.setReverse(revTwo);

                    for (Iterator it = props.iterator(); it.hasNext(); ) {
                        PropertyNd prop = (PropertyNd) it.next();
                        define(ot, prop);
                    }

                    Link l = new Link(rone.getName(), rtwo, rone,
                                      one.isCollection(),
                                      one.isNullable());
                    m_symbols.setLocation(l, one);
                    m_links.add(l);
                    twoot.addProperty(l);
                    l = new Link(rtwo.getName(), rone, rtwo,
                                 two.isCollection(), two.isNullable());
                    m_symbols.setLocation(l, two);
                    m_links.add(l);
                    oneot.addProperty(l);
                } else {
                    Role rone = define(oneot, two);
                    Role rtwo = define(twoot, one);
                    if (rone != null && rtwo != null) {
                        rone.setReverse(rtwo);
                    }
                }
            }
        }, new Node.IncludeFilter(new Node.Field[] {
            AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.PROPERTIES,
            FileNd.ASSOCIATIONS
        }));

        m_errors.check();

        for (Iterator it = m_symbols.getObjectTypes().iterator();
             it.hasNext(); ) {
            ObjectTypeNd otn = (ObjectTypeNd) it.next();
            ObjectType ot = m_symbols.getEmitted(otn);
            emit(otn, ot);
            m_root.addObjectType(ot);
        }

        m_ast.traverse(new Node.Switch() {
            public void onObjectType(ObjectTypeNd otn) {
                ObjectMap om = new ObjectMap(m_symbols.getEmitted(otn));
                m_root.addObjectMap(om);
                m_symbols.setLocation(om, otn);
            }
            public void onAssociation(AssociationNd assn) {
                ObjectType ot = m_symbols.getEmitted(linkName(assn));
                if (ot != null) {
                    ObjectMap om = new ObjectMap(ot);
                    m_root.addObjectType(ot);
                    m_root.addObjectMap(om);
                    m_symbols.setLocation(om, assn);
                }
            }
        });

        final boolean[] modified = { false };
        do {
            modified[0] = false;
            m_ast.traverse(new Node.Switch() {
                public void onNestedMap(NestedMapNd nm) {
                    ObjectMap om = getMap(nm);
                    if (om == null) {
                        ObjectMap container =
                            getMap(nm.getParent().getParent());
                        if (container != null) {
                            ObjectType type = container
                                .getObjectType()
                                .getProperty(getPath(nm.getParent()))
                                .getType();
                            om = new ObjectMap(type);
                            emit(nm, om);
                            modified[0] = true;
                        }
                    }
                }
            });
        } while (modified[0]);

        emitDDL();

        m_errors.check();

        emitMapping();

        m_errors.check();

        emitEvents();

        m_errors.check();

        emitDataOperations();

        m_errors.check();

	m_ast.traverse(new Node.Switch() {
            public void onObjectType(ObjectTypeNd nd) {
                ObjectType ot = m_symbols.getEmitted(nd);
                JavaClassNd jcn = nd.getJavaClass();
                JavaClassNd acn = nd.getAdapterClass();

                if (jcn == null ) { return; }

                Class javaClass;
                try {
                    javaClass = Class.forName(jcn.getName());
                } catch (ClassNotFoundException e) {
                    m_errors.fatal(jcn,
                                   "Misspelled or non-existent class: " +
                                   jcn.getName());
                    return;
                }

                ot.setJavaClass(javaClass);

                if (acn == null) { return; }

                try {
                    Class adapterClass = Class.forName(acn.getName());
                    Adapter ad = (Adapter) adapterClass.newInstance();
                    m_root.addAdapter(javaClass, ad);
                } catch (IllegalAccessException e) {
                    m_errors.fatal
                        (acn, "illegal access: " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    m_errors.fatal
                        (acn, "class not found: " + e.getMessage());
                } catch (InstantiationException e) {
                    m_errors.fatal
                        (acn, "instantiation exception: " +
                         e.getMessage());
                }
            }
        });

	m_errors.check();

        m_ast.traverse(new Node.Switch() {
            public void onSuper(SuperNd sn) {
                m_errors.warn
                    (sn, "super is no longer necessary and deprecated, " +
                     "please remove");
            }
        });

        m_errors.check();

        m_ast.traverse(new Node.Switch() {
            private void type(ObjectType type, Column col) {
                if (col.getType() == Integer.MIN_VALUE) {
                    Adapter ad = m_root.getAdapter(type.getJavaClass());
                    col.setType(ad.defaultJDBCType());
                }
            }
            private void type(final ObjectType type, Node mapping) {
                if (mapping == null) { return; }
                mapping.dispatch(new Node.Switch() {
                    public void onColumn(ColumnNd colnd) {
                        type(type, lookup(colnd));
                    }
                    public void onPropertyMapping(PropertyMappingNd pmn) {
                        if (pmn.isValue()) {
                            Column[] cols = getColumns(pmn);
                            for (int i = 0; i < cols.length; i++) {
                                type(type, cols[i]);
                            }
                        }
                    }
                });
            }
            public void onProperty(PropertyNd pnd) {
                ObjectType ot = m_symbols.getEmitted(pnd.getType());
                type(ot, pnd.getMapping());
            }
            public void onNestedMapping(NestedMappingNd nm) {
                ObjectMap map = getMap(nm.getParent());
                ObjectType container = map.getObjectType();
                Path p = nm.getPath().getPath();
                Property prop = container.getProperty(p);
                type(prop.getType(), nm.getMapping());
            }
        });

	propogateTypes();

        m_errors.check();

        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            reverse((ObjectType) it.next());
        }

        m_errors.check();
    }

    private void reverse(ObjectType type) {
        for (Iterator it = type.getProperties().iterator(); it.hasNext(); ) {
            reverse((Property) it.next());
        }
    }

    private static int s_revid = 0;

    private void reverse(Property p) {
        if (!(p instanceof Role)) { return; }
        Role r = (Role) p;
        if (!r.isReversable() && r.getType().isKeyed()) {
            ObjectMap om = m_root.getObjectMap(p.getContainer());
            Mapping m = om.getMapping(Path.get(p.getName()));
            if (m instanceof Static) { return; }
            Role rev = new Role("~rev" + s_revid++, p.getContainer(),
                                false, (m instanceof JoinThrough)
                                || (m instanceof JoinTo), true);
            r.getType().addProperty(rev);
            r.setReverse(rev);
            ObjectMap map = m_root.getObjectMap(r.getType());
            map.addMapping(m.reverse(Path.get(rev.getName())));
        }
    }

    // XXX: Commented out on this branch as this is the only
    // dependency on the part of core not in the branch spec.
    /*
      public void emitVersioned() {
      VersioningMetadata.NodeSwitch nodeSwitch =
      VersioningMetadata.getVersioningMetadata().nodeSwitch(m_emitted);
      m_ast.traverse(nodeSwitch);
      nodeSwitch.onFinish();
      }
    */

    private HashMap m_primaryKeys = new HashMap();

    private UniqueKey unique(Node nd, Column[] cols, boolean primary) {
        Table table = cols[0].getTable();
        if (table.getUniqueKey(cols) != null) {
            // Can't warn about this yet since we have no syntax that
            // lets you not specify duplicate keys.
            //m_errors.warn(nd, "duplicate key");
            return table.getUniqueKey(cols);
        }
        UniqueKey key;
        try {
            key = new UniqueKey(table, null, cols);
        } catch (IllegalArgumentException e) {
            m_errors.fatal(nd, e.getMessage());
            return null;
        }
        if (primary) {
            UniqueKey pk = table.getPrimaryKey();
            if (pk != null) {
                Node prev = (Node) m_primaryKeys.get(pk);
                m_errors.warn
                    (nd, "table already has primary key: " +
                     prev.getLocation());
                if (prev instanceof ObjectKeyNd) {
                    return key;
                }
            }
            table.setPrimaryKey(key);
            m_primaryKeys.put(key, nd);
            m_symbols.setLocation(table, nd);
        }
        return key;
    }

    private UniqueKey unique(Node nd, Collection ids, boolean primary) {
        final ArrayList columns = new ArrayList();
        final Node.Traversal firstColumn = new Node.Traversal() {
            public boolean accept(Node child) {
                Node.Field f = child.getField();
                if (f == PropertyNd.MAPPING) {
                    return true;
                } else if (f == PropertyMappingNd.COLUMNS) {
                    return true;
                } else if (f == JoinPathNd.JOINS) {
                    return child.getIndex() == 0;
                } else {
                    return f == JoinNd.FROM;
                }
            }

            public void onColumn(ColumnNd colnd) {
                columns.add(lookup(colnd));
            }
        };

        if (nd instanceof ObjectKeyNd || nd instanceof UniqueKeyNd) {
            ObjectMap om = getMap(nd.getParent());
            ObjectType ot = om.getObjectType();
            for (Iterator it = ids.iterator(); it.hasNext(); ) {
                String id = ((IdentifierNd) it.next()).getName();
                Property prop = (Property) ot.getProperty(id);
                if (prop == null) {
                    m_errors.warn(nd, "no such property " + id);
                    return null;
                }
                PropertyNd propNd = (PropertyNd) getNode(prop);
                int start = columns.size();
                propNd.traverse(firstColumn);
                int end = columns.size();
                if (start == end) {
                    m_errors.warn(nd, "no metadata for " + id);
                    return null;
                }
            }
        } else if (nd instanceof PropertyNd) {
            nd.traverse(firstColumn);
            if (columns.size() == 0) {
                m_errors.warn(nd, "no metadata for " +
                              ((PropertyNd) nd).getName().getName());
                return null;
            }
        } else {
            throw new IllegalArgumentException("node type: " + nd.getClass());
        }

        if (columns.size() == 0) {
            throw new IllegalStateException("did not error out earlier");
        }
        Column[] cols = new Column[columns.size()];
        for (int i = 0; i < cols.length; i++) {
            cols[i] = (Column) columns.get(i);
        }
        return unique(nd, cols, primary);
    }

    private class NestedKeyTraversal extends Node.Traversal {
        private ObjectKeyNd m_key = null;
        private List m_columns = null;
        private Set m_ids = null;
        public void onObjectKey(ObjectKeyNd okn) {
            m_key = okn;
            m_columns = new ArrayList();
            m_ids = new HashSet();
            List ids = (List) okn.get(okn.PROPERTIES);
            for (int i = 0; i < ids.size(); i++) {
                IdentifierNd idn = (IdentifierNd) ids.get(i);
                m_ids.add(idn.getName());
            }
            m_key.getParent().getParent().traverse(this);
        }
        public void onColumn(ColumnNd nd) {
            m_columns.add(lookup(nd));
        }
        public boolean accept(Node child) {
            Node.Field f = child.getField();
            if (child instanceof ObjectKeyNd) {
                return m_key == null;
            } else if (f == NestedMapNd.MAPPINGS) {
                return m_ids.contains
                    (((NestedMappingNd) child).getPath().getPath().getPath());
            } else if (f == JoinPathNd.JOINS) {
                return m_columns.isEmpty() ? child.isFirst() : child.isLast();
            } else if (f == JoinNd.FROM) {
                return !m_columns.isEmpty();
            } else if (f == JoinNd.TO) {
                return m_columns.isEmpty();
            } else if (f == PropertyMappingNd.COLUMNS) {
                // XXX: does this work for join throughs?
                return true;
            } else {
                return true;
            }
        }
        public Column[] getColumns() {
            return (Column[]) m_columns.toArray(new Column[m_columns.size()]);
        }
    }

    private void emitDDL() {
        final HashMap tables = new HashMap();
        for (Iterator it = m_root.getTables().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            tables.put(table.getName(), table);
        }

        m_ast.traverse(new Node.Switch() {
            public void onColumn(ColumnNd colNd) {
                Table table =
                    (Table) tables.get(colNd.getTable().getName());
                if (table == null) {
                    table = new Table(colNd.getTable().getName());
                    tables.put(table.getName(), table);
                    m_symbols.setLocation(table, colNd);
                }

                DbTypeNd type = colNd.getType();
                Column col = table.getColumn(colNd.getName().getName());

                if (col == null) {
                    col = new Column(colNd.getName().getName());
                    table.addColumn(col);
                    m_symbols.setLocation(col, colNd);
                }

                if (type != null) {
                    col.setType(type.getType());
                    col.setSize(type.getSize());
                    col.setScale(type.getScale());
                }
            }
        });

        for (Iterator it = tables.values().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            if (table.getRoot() == null) {
                m_root.addTable(table);
            }
        }

        m_ast.traverse(new Node.Switch() {
            public void onObjectKey(ObjectKeyNd nd) {
                Node parent = nd.getParent();
                UniqueKey key;
                if (parent instanceof ObjectTypeNd) {
                    key = unique(nd, nd.getProperties(), true);
                } else if (parent instanceof NestedMapNd) {
                    NestedKeyTraversal nkt = new NestedKeyTraversal();
                    nd.traverse(nkt);
                    key = unique(nd, nkt.getColumns(), true);
                } else {
                    throw new IllegalStateException();
                }
                if (key != null) {
                    ObjectMap om = getMap(parent);
                    om.setTable(key.getTable());
                }
            }

            public void onUniqueKey(UniqueKeyNd nd) {
                unique(nd, nd.getProperties(), false);
            }

            public void onReferenceKey(ReferenceKeyNd nd) {
                UniqueKey key = unique
                    (nd, new Column[] { lookup(nd.getCol()) }, true);
                if (key != null) {
                    ObjectMap om = getMap(nd.getParent());
                    om.setTable(key.getTable());
                }
            }

            public void onProperty(PropertyNd nd) {
                if (nd.isUnique()) {
                    ArrayList ids = new ArrayList();
                    ids.add(nd.getName());
                    unique(nd, ids, false);
                }
            }
        });

	m_ast.traverse(new Node.Switch() {
            public void onJoinPath(JoinPathNd jpn) {
                List joins = jpn.getJoins();
                if (joins.size() != 2) {
                    return;
                }

                ColumnNd from = ((JoinNd) joins.get(0)).getTo();
                ColumnNd to = ((JoinNd) joins.get(1)).getFrom();

                boolean collection = true;
                Object obj = getEmitted(jpn.getParent());
                if (obj != null && obj instanceof Property) {
                    Property prop = (Property) obj;
                    if (!prop.isCollection()) {
                        collection = false;
                    }
                }

                if (collection) {
                    unique(jpn, new Column[] { lookup(from), lookup(to) },
                           true);
                } else {
                    unique(jpn, new Column[] { lookup(from) }, true);
                }
            }
        }, new Node.IncludeFilter(new Node.Field[] {
            AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.PROPERTIES,
            FileNd.ASSOCIATIONS, AssociationNd.PROPERTIES,
            AssociationNd.ROLE_ONE, PropertyNd.MAPPING
        }));

        m_ast.traverse(new Node.Switch() {
            public void onJoinPath(JoinPathNd jpn) {
                List joins = jpn.getJoins();
                if (joins.size() != 1) {
                    m_errors.fatal
                        (jpn, "only length 1 join paths allowed here");
                    return;
                }

                JoinNd join = (JoinNd) joins.get(0);
                ColumnNd fromNd = join.getFrom();
                ColumnNd toNd = join.getTo();
                Column from = lookup(fromNd);
                Column to = lookup(toNd);

                if (from.isUniqueKey()) {
                    unique(toNd, new Column[] { to }, true);
                    fk(join, false);
                } else if (to.isUniqueKey()) {
                    unique(fromNd, new Column[] { from }, true);
                    fk(join, true);
                } else {
                    m_errors.fatal
                        (jpn, "join path does not connect to a primary key");
                    return;
                }
            }
        }, new Node.IncludeFilter(new Node.Field[] {
            AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.JOIN_PATHS
        }));
    }

    private Path getPath(Node nd) {
        if (nd instanceof NestedMappingNd) {
            return ((NestedMappingNd) nd).getPath().getPath();
        } else if (nd instanceof PropertyNd) {
            return Path.get(((PropertyNd) nd).getName().getName());
        } else {
            throw new IllegalArgumentException
                ("don't know how to handle: " + nd.getClass());
        }
    }

    private ObjectMap getMap(Node nd) {
        if (nd instanceof ObjectTypeNd) {
            return m_root.getObjectMap
                (m_symbols.getEmitted((ObjectTypeNd) nd));
        } else if (nd instanceof NestedMapNd) {
            return (ObjectMap) getEmitted(nd);
        } else {
            throw new IllegalArgumentException
                ("don't know how to handle node of type: " + nd.getClass());
        }
    }

    private void emitMapping() {
	m_ast.traverse(new Node.Switch() {
            public void onReferenceKey(ReferenceKeyNd rkn) {
                Column key = lookup(rkn.getCol());
                ObjectMap om = getMap(rkn.getParent());
                Column[] cols = new Column[] {key};
                if (key.getTable().getForeignKey(cols) == null) {
                    ObjectMap sm = om.getSuperMap();
                    Table table = null;
                    while (sm != null) {
                        table = sm.getTable();
                        if (table != null) {
                            break;
                        }
                        sm = sm.getSuperMap();
                    }
                    if (table == null) {
                        throw new IllegalStateException
                            ("unable to find supertable for " +
                             om.getObjectType());
                    }
                    fk(cols, table.getPrimaryKey());
                }
            }
        });

        m_ast.traverse(new Node.Switch() {
            public void onIdentifier(IdentifierNd id) {
                ObjectMap om = getMap(id.getParent().getParent());
                ObjectType ot = om.getObjectType();
                Role role = (Role) ot.getProperty(id.getName());
                if (role == null) {
                    m_errors.fatal
                        (id, "no such property: " + id.getName());
                } else {
                    if (role.isCollection()) {
                        m_errors.fatal
                            (id, "collections cannot be keys: " +
                             id.getName());
                    } else {
                        om.getKeyProperties().add(role);
                        role.setNullable(false);
                    }
                }
            }
        }, new Node.IncludeFilter(new Node.Field[] {
            AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.PROPERTIES,
            PropertyNd.NESTED_MAP, NestedMapNd.OBJECT_KEY,
            NestedMapNd.MAPPINGS, NestedMappingNd.NESTED_MAP,
            ObjectTypeNd.OBJECT_KEY, ObjectKeyNd.PROPERTIES
        }));

	m_ast.traverse(new Node.Switch() {
            public void onAssociation(AssociationNd assn) {
                if (assn.getProperties().size() == 0) {
                    return;
                }

                PropertyNd one = assn.getRoleOne();
                PropertyNd two = assn.getRoleTwo();
                ObjectType ot = m_symbols.getEmitted(linkName(assn));
                ObjectMap om = m_root.getObjectMap(ot);
                Collection keys = om.getKeyProperties();
                Role pone = (Role) ot.getProperty(one.getName().getName());
                Role ptwo = (Role) ot.getProperty(two.getName().getName());
                Link lone = (Link)
                    ptwo.getType().getProperty(pone.getName());
                Link ltwo = (Link)
                    pone.getType().getProperty(ptwo.getName());
                keys.add(ptwo);
                keys.add(pone);

                if (one.getMapping() != null) {
                    emitMapping(pone, (JoinPathNd) one.getMapping(), 1, 2);
                    emitMapping(pone.getReverse(),
                                (JoinPathNd) two.getMapping(), 0, 1);
                    emitMapping(ltwo, (JoinPathNd) two.getMapping(), 0, 2);
                } else {
                    om.addMapping(new Static(Path.get(pone.getName())));
                    ObjectMap oneom = m_root.getObjectMap(pone.getType());
                    oneom.addMapping
                        (new Static
                         (Path.get(pone.getReverse().getName())));
                    oneom.addMapping(new Static(Path.get(ltwo.getName())));
                }

                if (two.getMapping() != null) {
                    emitMapping(ptwo, (JoinPathNd) two.getMapping(), 1, 2);
                    emitMapping(ptwo.getReverse(),
                                (JoinPathNd) one.getMapping(), 0, 1);
                    emitMapping(lone, (JoinPathNd) one.getMapping(), 0, 2);
                } else {
                    om.addMapping(new Static(Path.get(ptwo.getName())));
                    ObjectMap twoom = m_root.getObjectMap(ptwo.getType());
                    twoom.addMapping
                        (new Static
                         (Path.get(ptwo.getReverse().getName())));
                    twoom.addMapping(new Static(Path.get(lone.getName())));
                }

                String[] paths = new String[] { pone.getName(),
                                                ptwo.getName() };
                for (int i = 0; i < paths.length; i++) {
                    Mapping m = om.getMapping(Path.get(paths[i]));
                    if (m.getTable() != null) {
                        om.setTable(m.getTable());
                        break;
                    }
                }
            }
        });

        m_ast.traverse(new Node.Switch() {
            public void onProperty(PropertyNd pn) {
                emit(pn);
                Node mapping = getMapping(pn);
                Role prop = (Role) getEmitted(pn);
                // auto generate reverse way for one-way composites
                if (pn.isComposite()
                    && pn.getParent() instanceof ObjectTypeNd) {
                    if (mapping == null) {
                        m_errors.fatal
                            (pn, "one-way composite must have metadata");
                    } else {
                        Role rev = prop.getReverse();
                        if (mapping instanceof JoinPathNd) {
                            emitMapping(rev, (JoinPathNd) mapping, true);
                        } else {
                            m_errors.fatal(pn, "must have a join path");
                        }
                    }
                }
            }
            public void onNestedMapping(NestedMappingNd nm) {
                emit(nm);
            }
            private Node getMapping(Node nd) {
                if (nd instanceof PropertyNd) {
                    return (Node) nd.get(PropertyNd.MAPPING);
                } else if (nd instanceof NestedMappingNd) {
                    return (Node) nd.get(NestedMappingNd.MAPPING);
                } else {
                    throw new IllegalArgumentException("" + nd);
                }
            }
            private NestedMapNd getNestedMap(Node nd) {
                if (nd instanceof PropertyNd) {
                    return (NestedMapNd) nd.get(PropertyNd.NESTED_MAP);
                } else if (nd instanceof NestedMappingNd) {
                    return (NestedMapNd) nd.get(NestedMappingNd.NESTED_MAP);
                } else {
                    throw new IllegalArgumentException("" + nd);
                }
            }
            private void emit(Node nd) {
                final ObjectMap om;
                if (nd.getParent() instanceof AssociationNd) {
                    Property p = (Property) getEmitted(nd);
                    if (p == null) { return; }
                    om = m_root.getObjectMap(p.getContainer());
                } else {
                    om = getMap(nd.getParent());
                }
                if (om == null) { return; }
                final Role prop =
                    (Role) om.getObjectType().getProperty(getPath(nd));
                if (prop == null) { return; }

                Node mapping = getMapping(nd);
                if (mapping == null) {
                    Path p = Path.get(prop.getName());
                    om.addMapping(new Static(p));
                } else {
                    mapping.dispatch(new Node.Switch() {
                        public void onColumn(ColumnNd col) {
                            emitMapping(om, prop, col);
                        }
                        public void onPropertyMapping(PropertyMappingNd pmn) {
                            emitMapping(om, prop, pmn);
                        }
                        public void onJoinPath(JoinPathNd jn) {
                            emitMapping(om, prop, jn);
                        }
                        public void onQualias(QualiasNd q) {
                            emitMapping(om, prop, q);
                        }
                    });
                }
                Mapping m = om.getMapping(prop);
                if (m != null) {
                    NestedMapNd nm = getNestedMap(nd);
                    if (nm == null && prop.getType().isPrimitive()) {
                        m.setMap(new ObjectMap(prop.getType()));
                    } else if (nm != null) {
                        m.setMap(getMap(nm));
                    }
                }
            }
        });

        m_ast.traverse(new Node.Switch() {
            public void onPath(PathNd nd) {
                ObjectMap om = m_root.getObjectMap
                    (m_symbols.getEmitted
                     ((ObjectTypeNd) nd.getParent().getParent()));
                om.addFetchedPath(nd.getPath());
            }
        }, new Node.IncludeFilter(new Node.Field[] {
            AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.AGGRESSIVE_LOAD,
            AggressiveLoadNd.PATHS
        }));
    }

    private void propogateTypes() {
	int before;
	do {
	    before = m_fks.size();
	    for (Iterator it = m_fks.iterator(); it.hasNext(); ) {
		ForeignKey fk = (ForeignKey) it.next();
		Column[] cols = fk.getColumns();
		Column[] to = fk.getUniqueKey().getColumns();
		for (int i = 0; i < cols.length; i++) {
		    if (cols[i].getType() == Integer.MIN_VALUE &&
			to[i].getType() != Integer.MIN_VALUE) {
			cols[i].setType(to[i].getType());
			cols[i].setSize(to[i].getSize());
			cols[i].setScale(to[i].getScale());
			it.remove();
		    }
		}
	    }
	} while (m_fks.size() < before);
    }

    private ObjectMap om(Property p) {
        return m_root.getObjectMap(p.getContainer());
    }

    private void emitMapping(Property prop, ColumnNd colNd) {
        emitMapping(om(prop), prop, colNd);
    }

    private HashMap m_propByColumns = new HashMap();

    private void emitMapping(ObjectMap om, Property prop, ColumnNd colNd) {
	if (prop.getType().isKeyed()) {
	    m_errors.fatal(colNd, "association requires a join path");
	}

        Column col = lookup(colNd);

        Property prev = (Property) m_propByColumns.get(col);
        if (prev == null) {
            m_propByColumns.put(col, prop);
        } else {
            ObjectType ot1 = prop.getContainer();
            ObjectType ot2 = prev.getContainer();
            if ((ot1.isSubtypeOf(ot2) || ot2.isSubtypeOf(ot1))
                && !prop.equals(prev)) {
                m_errors.fatal(colNd, "column already mapped to " + prev);
            }
        }

        Value m = new Value(Path.get(prop.getName()), col);
        om.addMapping(m);
        if (prop.isNullable()) { col.setNullable(true); }
    }

    private ObjectMap getTarget(PropertyMappingNd pmn) {
        Node parent = pmn.getParent();
        final ObjectMap[] result = { null };
        parent.dispatch(new Node.Switch() {
            public void onProperty(PropertyNd pn) {
                if (pn.getNestedMap() != null) {
                    result[0] = getMap(pn.getNestedMap());
                } else {
                    Property prop = (Property) getEmitted(pn);
                    result[0] = m_root.getObjectMap(prop.getType());
                }
            }
            public void onNestedMapping(NestedMappingNd nmn) {
                if (nmn.getNestedMap() != null) {
                    result[0] = getMap(nmn.getNestedMap());
                } else {
                    ObjectType type = getMap(nmn.getParent()).getObjectType();
                    Property prop = type.getProperty(nmn.getPath().getPath());
                    result[0] = m_root.getObjectMap(prop.getType());
                }
            }
        });
        return result[0];
    }

    private Column[] getColumns(PropertyMappingNd pmn, int start, int end) {
        List colnds = pmn.getColumns();
        Column[] result = new Column[end - start];
        for (int i = start; i < end; i++) {
            result[i - start] = lookup((ColumnNd) colnds.get(i));
        }
        return result;
    }

    private Column[] getColumns(PropertyMappingNd pmn) {
        return getColumns(pmn, 0, pmn.getColumns().size());
    }

    private void emitMapping(ObjectMap om, Property prop,
                             PropertyMappingNd pmn) {
        if (pmn.isValue()) {
            // XXX: multi column value mappings ignored
            emitMapping(om, prop, (ColumnNd) pmn.getColumns().get(0));
        } else {
            ObjectMap tgt = getTarget(pmn);
            Role role = (Role) prop;
            Path p = Path.get(prop.getName());
            Mapping m;
            if (pmn.isReference()) {
                ForeignKey fk =
                    fk(pmn, getColumns(pmn), tgt.getTable().getPrimaryKey());
                m = new JoinTo(p, fk);
                setNullable(fk, prop.isNullable());
            } else if (pmn.isInverse()) {
                ForeignKey fk =
                    fk(pmn, getColumns(pmn), om.getTable().getPrimaryKey());
                m = new JoinFrom(p, fk);
                if (!role.isReversable()) {
                    setNullable(fk, prop.isNullable());
                }
            } else if (pmn.isMapping()) {
                UniqueKey from = om.getTable().getPrimaryKey();
                UniqueKey to = tgt.getTable().getPrimaryKey();
                m = new JoinThrough
                    (p, fk(pmn, getColumns(pmn, 0, from.getColumns().length),
                           from),
                     fk(pmn, getColumns(pmn, from.getColumns().length,
                                        pmn.getColumns().size()),
                        to));
            } else {
                throw new IllegalStateException("bad pmn");
            }
            if (role.isReversable()) {
                Role rev = role.getReverse();
                emitMapping(pmn, tgt, rev, m.reverse(Path.get(rev.getName())));
            }
            emitMapping(pmn, om, prop, m);
        }
    }

    private void emitMapping(Node nd, ObjectMap map, Property p, Mapping m) {
        Mapping prev = map.getMapping(p);
        if (prev != null) {
            // XXX:
            //m_errors.fatal(nd, "duplicate mapping, previously defined: " +
            //getNode(prev).getLocation());
            return;
        }
        map.addMapping(m);
        emit(nd, m);
    }

    private ForeignKey fk(JoinNd jn, boolean forward) {
        ColumnNd fromnd;
        ColumnNd tond;

        if (forward) {
            fromnd = jn.getFrom();
            tond = jn.getTo();
        } else {
            fromnd = jn.getTo();
            tond = jn.getFrom();
        }

        Column from = lookup(fromnd);
        Column to = lookup(tond);

        ForeignKey fk = from.getTable().getForeignKey(new Column[] {from});
        UniqueKey uk = to.getTable().getUniqueKey(new Column[] {to});
        if (uk == null) {
            m_errors.fatal(tond, "not a unique key: " + to);
            return null;
        }

        if (fk != null) {
            if (uk.equals(fk.getUniqueKey())) {
                return fk;
            } else {
                Node prev = getNode(fk);
                m_errors.fatal
                    (fromnd,
                     "foreign key incompatible with previous definition: " +
                     prev.getLocation());
                return null;
            }
        }

        return fk(fromnd, new Column[] {from}, uk);
    }

    private ForeignKey fk(Node nd, Column[] from, UniqueKey key) {
        ForeignKey fk = from[0].getTable().getForeignKey(from);
        if (fk != null && fk.getUniqueKey().equals(key)) {
            Node prev = getNode(fk);
            // XXX:
            //m_errors.fatal
            //(nd, "duplicate foreign key definition, previously defined: " +
            //prev.getLocation());
            return fk;
        }
        fk = fk(from, key);
        emit(nd, fk);
	return fk;
    }

    private ForeignKey fk(Column[] cols, UniqueKey uk) {
	Column[] to = uk.getColumns();
	if (cols.length != to.length) {
	    throw new IllegalArgumentException
		("foreign key length must match unique key length");
	}
	ForeignKey fk =
	    new ForeignKey(cols[0].getTable(), null, cols, uk, false);
	m_fks.add(fk);
        return fk;
    }

    private void emitMapping(Role prop, JoinPathNd jpn) {
	emitMapping(om(prop), prop, jpn);
    }

    private void emitMapping(ObjectMap om, Role prop, JoinPathNd jpn) {
	emitMapping(om, prop, jpn, false);
    }

    private void emitMapping(Role prop, JoinPathNd jpn,
                             boolean reverse) {
        emitMapping(om(prop), prop, jpn, reverse);
    }

    private void emitMapping(ObjectMap om, Role prop, JoinPathNd jpn,
                             boolean reverse) {
        if (reverse) {
            emitMapping(om, prop, jpn, jpn.getJoins().size(), 0);
        } else {
            emitMapping(om, prop, jpn, 0, jpn.getJoins().size());
        }
    }

    private void emitMapping(Property prop, JoinPathNd jpn, int start,
                             int stop) {
        emitMapping(om(prop), prop, jpn, start, stop);
    }

    private void emitMapping(ObjectMap om, Property prop, JoinPathNd jpn,
                             int start, int stop) {
        Path path = Path.get(prop.getName());
        List joins = jpn.getJoins();
        Mapping m;

        int magnitude = Math.abs(stop - start);
        boolean forward = stop > start;
        int low = forward ? start : stop;
        boolean joinForward;

        if (magnitude == 1) {
            JoinNd jn = (JoinNd) joins.get(low);
            Column to = lookup(jn.getTo());
            Column from = lookup(jn.getFrom());
            if (to.isPrimaryKey()) {
                joinForward = true;
            } else if (from.isPrimaryKey()) {
                joinForward = false;
            } else if (to.isUniqueKey()) {
                joinForward = true;
            } else if (from.isUniqueKey()) {
                joinForward = false;
            } else {
                m_errors.fatal(jpn, "neither end unique");
                return;
            }

            ForeignKey fk = fk(jn, joinForward);

            if (fk == null) {
                return;
            }

            if (forward == joinForward) {
                m = new JoinTo(path, fk);
                setNullable(fk, prop.isNullable());
            } else {
                m = new JoinFrom(path, fk);
                if (!((Role) prop).isReversable()) {
                    setNullable(fk, prop.isNullable());
                }
            }
        } else if (magnitude == 2) {
            JoinNd first = (JoinNd) joins.get(low);
            JoinNd second = (JoinNd) joins.get(low + 1);
            ForeignKey from = fk(first, !forward);
            ForeignKey to = fk(second, forward);

	    if (from == null || to == null) {
                // should already be an error condition
		return;
	    }

            if (forward) {
                m = new JoinThrough(path, from, to);
            } else {
                m = new JoinThrough(path, to, from);
            }
            if (prop instanceof Role) {
                setNullable(from, false);
                setNullable(to, false);
            }
        } else {
            m_errors.fatal(jpn, "bad join path");
            return;
        }

        om.addMapping(m);
    }

    private void setNullable(Constraint c, boolean value) {
        if (value) {
            Column[] cols = c.getColumns();
            for (int i = 0; i < cols.length; i++) {
                cols[i].setNullable(true);
            }
        }
    }

    private Column lookup(ColumnNd colNd) {
        Table table = m_root.getTable(colNd.getTable().getName());
        return table.getColumn(colNd.getName().getName());
    }

    private void emitMapping(Property prop, QualiasNd nd) {
        emitMapping(om(prop), prop, nd);
    }

    private void emitMapping(ObjectMap om, Property prop, QualiasNd nd) {
        Qualias q = new Qualias(Path.get(prop.getName()), nd.getQuery());
        om.addMapping(q);
    }

    private void emitEvents() {
        m_ast.traverse(new Node.Switch() {
            public void onEvent(EventNd nd) {
                emitEvent(nd);
            }
        });
    }

    private void emitEvent(EventNd ev) {
	String givenName = null;
	if (ev.getName() != null) {
	    givenName = ev.getName().getName();
	}

	ObjectType ot;
	EventNd.Type type;
	String name;

        if (ev.getParent() instanceof ObjectTypeNd) {
            ObjectTypeNd otn = (ObjectTypeNd) ev.getParent();
	    ot = m_symbols.getEmitted(otn);
	    type = ev.getType();
	    name = givenName;
        } else {
            AssociationNd assn = (AssociationNd) ev.getParent();
            String one = assn.getRoleOne().getName().getName();
            String two = assn.getRoleTwo().getName().getName();
	    ObjectType oneType =
		m_symbols.getEmitted(assn.getRoleOne().getType());
	    ObjectType twoType =
		m_symbols.getEmitted(assn.getRoleTwo().getType());

	    if (assn.getProperties().size() > 0) {
		ot = m_symbols.getEmitted(linkName(assn));
                boolean warnDups = true;
		if ((givenName == null ||
		     givenName.equals(one) ||
		     givenName.equals(two)) &&
		    ev.getType().equals(EventNd.ADD)) {
		    type = EventNd.INSERT;
		    name = null;
		} else if ((givenName == null ||
			    givenName.equals(one) ||
			    givenName.equals(two)) &&
			   ev.getType().equals(EventNd.REMOVE)) {
		    type = EventNd.DELETE;
		    name = null;
		} else {
		    type = ev.getType();
		    name = givenName;
                    warnDups = false;
		}
                if (warnDups) {
                    Collection blocks = getSQLBlocks(ot, type, name);
                    if (blocks != null && blocks.size() > 0) {
                        m_errors.warn
                            (ev, "redundant events specified," +
                             " ignoring this event");
                        return;
                    }
                }
	    } else if (givenName == null || givenName.equals(one)) {
		if (givenName != null && !ev.isSingle()) {
		    Collection blocks = getSQLBlocks(oneType, ev.getType(),
						     two);
		    if (blocks != null && blocks.size() > 0) {
			m_errors.warn
			    (ev, "both ends of a two way specified, " +
			     "ignoring this event");
			return;
		    }
		}

		ot = twoType;
		type = ev.getType();
		name = one;
	    } else if (givenName.equals(two)) {
		if (!ev.isSingle()) {
		    Collection blocks = getSQLBlocks(twoType, ev.getType(),
						     one);
		    if (blocks != null && blocks.size() > 0) {
			m_errors.warn
			    (ev, "both ends of a two way specified, " +
			     "ignoring this event");
			return;
		    }
		}

		ot = oneType;
		type = ev.getType();
		name = two;
	    } else {
                m_errors.warn
                    (ev.getName(), "no such role: " + ev.getName().getName());
		return;
            }
        }

	addEvent(m_root.getObjectMap(ot), ev, type, name);
    }

    private Collection getSQLBlocks(ObjectType ot, EventNd.Type type,
				    String role) {
        return getSQLBlocks(ot.getRoot().getObjectMap(ot), type, role);
    }


    private void setSQLBlocks(ObjectType ot, EventNd nd, EventNd.Type type,
                              String role, Collection blocks) {
        setSQLBlocks(ot.getRoot().getObjectMap(ot), nd, type, role, blocks);
    }

    private Collection getSQLBlocks(ObjectMap om, EventNd.Type type,
				    String role) {
        Collection blocks;
        if (type.equals(EventNd.INSERT)) {
            blocks = om.getDeclaredInserts();
        } else if (type.equals(EventNd.UPDATE)) {
            blocks = om.getDeclaredUpdates();
        } else if (type.equals(EventNd.DELETE)) {
            blocks = om.getDeclaredDeletes();
        } else if (type.equals(EventNd.RETRIEVE)) {
            if (role == null) {
                blocks = om.getDeclaredRetrieves();
            } else {
                throw new Error("single block event");
            }
        } else if (type.equals(EventNd.ADD)) {
            blocks = getMapping(om, role).getAdds();
        } else if (type.equals(EventNd.REMOVE)) {
            blocks = getMapping(om, role).getRemoves();
        } else if (type.equals(EventNd.CLEAR)) {
            blocks = new ArrayList();
        } else if (type.equals(EventNd.RETRIEVE_ATTRIBUTES)) {
            blocks = new ArrayList();
        } else {
            throw new IllegalArgumentException("bad event type: " + type);
        }

        return blocks;
    }


    private static Collection add(Collection a, Collection b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }

        ArrayList result = new ArrayList();
        result.addAll(a);
        result.addAll(b);
        return result;
    }

    private void setSQLBlocks(ObjectMap om, EventNd nd, EventNd.Type type,
                              String role, Collection blocks) {
        if (type.equals(EventNd.INSERT)) {
            if (checkDuplicates(om, om.getDeclaredInserts(), blocks, nd)) {
                om.setDeclaredInserts(blocks);
            }
        } else if (type.equals(EventNd.UPDATE)) {
            if (checkDuplicates(om, om.getDeclaredUpdates(), blocks, nd)) {
                om.setDeclaredUpdates(blocks);
            }
        } else if (type.equals(EventNd.DELETE)) {
            if (checkDuplicates(om, om.getDeclaredDeletes(), blocks, nd)) {
                om.setDeclaredDeletes(blocks);
            }
        } else if (type.equals(EventNd.RETRIEVE)) {
            if (role == null) {
                om.setDeclaredRetrieves
                    (add(blocks, om.getDeclaredRetrieves()));
            } else {
                throw new Error("single block event");
            }
        } else if (type.equals(EventNd.ADD)) {
            Mapping m = getMapping(om, role);
            if (checkDuplicates(om, m.getAdds(), blocks, nd)) {
                m.setAdds(blocks);
            }
        } else if (type.equals(EventNd.REMOVE)) {
            Mapping m = getMapping(om, role);
            if (checkDuplicates(om, m.getRemoves(), blocks, nd)) {
                m.setRemoves(blocks);
            }
        } else if (type.equals(EventNd.CLEAR)) {
            // do nothing
        } else if (type.equals(EventNd.RETRIEVE_ATTRIBUTES)) {
            om.setDeclaredRetrieves(add(om.getDeclaredRetrieves(), blocks));
        } else {
            throw new IllegalArgumentException("bad event type: " + type);
        }
    }

    private boolean checkDuplicates(ObjectMap om, Object prev, Object obj,
                                    EventNd nd) {
        if (prev != null) {
            Node prevNd = (Node) getNode(prev);
            m_errors.fatal
                (nd, "duplicate " + nd.getType() + " event for object type " +
                 om.getObjectType().getQualifiedName() +
                 ", previous definition: " + prevNd.getLocation());
            return false;
        } else {
            emit(nd, obj);
            return true;
        }
    }

    private void addEvent(ObjectMap om, EventNd ev, EventNd.Type type,
			  String role) {
        if (type.equals(EventNd.RETRIEVE) &&
            role != null) {
            Mapping m = getMapping(om, role);
            SQLBlock block = getBlock(om, ev);
            if (checkDuplicates(om, m.getRetrieve(), block, ev)) {
                m.setRetrieve(block);
            }
            return;
        } else if (type.equals(EventNd.RETRIEVE_ALL)) {
            SQLBlock block = getBlock(om, ev);
            if (checkDuplicates(om, om.getRetrieveAll(), block, ev)) {
                om.setRetrieveAll(block);
            }
            return;
        }

        ArrayList blocks = new ArrayList();

        for (Iterator it = ev.getSQL().iterator(); it.hasNext(); ) {
            SQLBlockNd nd = (SQLBlockNd) it.next();
            blocks.add(getBlock(om, nd));
        }

        setSQLBlocks(om, ev, type, role, blocks);
    }

    private SQLBlock getBlock(ObjectMap om, EventNd ev) {
        if (ev.getSQL().size() > 1) {
            m_errors.fatal(ev, "more than one sql block");
        }
        SQLBlockNd nd;
        Iterator it = ev.getSQL().iterator();
        if (it.hasNext()) {
            return getBlock(om, (SQLBlockNd) it.next());
        } else {
            return null;
        }
    }

    private SQLBlock getBlock(ObjectMap om, SQLBlockNd nd) {
        final ObjectType type = om == null ? null : om.getObjectType();

        try {
            SQLParser p = new SQLParser(new StringReader(nd.getSQL()));
            p.sql();
            final SQLBlock block = new SQLBlock(p.getSQL());
            m_symbols.setLocation(block, nd);
            for (Iterator ii = p.getAssigns().iterator(); ii.hasNext(); ) {
                SQLParser.Assign assn = (SQLParser.Assign) ii.next();
                block.addAssign(assn.getBegin(), assn.getEnd().getNext());
            }

            nd.traverse(new Node.Switch() {
                private void checkPath(Node nd, Path path) {
                    if (type != null && !type.exists(path)) {
                        m_errors.fatal
                            (nd, "no such path in " +
                             type.getQualifiedName() + ": " + path);
                    }
                }

                public void onMapping(MappingNd nd) {
                    Path col = nd.getCol().getPath();
                    col = Path.get(col.getName());
                    Path path = nd.getPath().getPath();
                    checkPath(nd, path);
                    block.addMapping(path, col);
                }

                public void onBinding(BindingNd nd) {
                    Path path = nd.getPath().getPath();
                    checkPath(nd, path);
                    block.addType(path, nd.getType().getType());
                }
            });
            return block;
        } catch (com.redhat.persistence.common.ParseException e) {
            m_errors.fatal(nd, e.getMessage());
            return null;
        }
    }

    private Mapping getMapping(ObjectMap om, String role) {
        Path path = Path.get(role);
        Mapping m = om.getMapping(path);
	if (m == null) {
	    throw new IllegalStateException
		("no mapping in type " + om.getObjectType() + " for role: " +
		 role);
	}
        return m;
    }

    private void emitDataOperations() {
        m_ast.traverse(new Node.Switch() {
            public void onDataOperation(DataOperationNd nd) {
                Path name = Path.get(nd.getFile().getModel().getName() +
                                     "." + nd.getName().getName());
                m_root.addDataOperation
                    (new DataOperation(name, getBlock(null, nd.getSQL())));
            }
        });
    }

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        for (int i = 0; i < args.length; i++) {
            pdl.load(new FileReader(args[i]), args[i]);
        }
        Root root = new Root();
        pdl.emit(root);
        OutputStreamWriter w = new OutputStreamWriter(System.out);
        print(root, w);
        w.flush();
    }

    private static void print(Root root, Writer w) {
        PDLWriter pw = new PDLWriter(w);
        pw.write(root);
    }

}
