package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.Adapter;
import com.arsdigita.persistence.proto.PropertyMap;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.pdl.nodes.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.math.*;
import java.sql.*;
import java.io.*;

import org.apache.log4j.Logger;

/**
 * PDL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/05/20 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/PDL.java#4 $ by $Author: ashah $, $DateTime: 2003/05/20 14:37:22 $";
    private final static Logger LOG = Logger.getLogger(PDL.class);

    private AST m_ast = new AST();
    private ErrorReport m_errors = new ErrorReport();
    private SymbolTable m_symbols = new SymbolTable(m_errors);
    private HashMap m_properties = new HashMap();
    private HashSet m_links = new HashSet();
    private HashSet m_fks = new HashSet();

    public PDL() {}

    public void load(Reader r, String filename) {
        try {
            PDLParser p = new PDLParser(r);
            FileNd file = p.file(filename);
            m_ast.add(AST.FILES, file);
        } catch (ParseException e) {
            throw new Error(filename + ": " + e.getMessage());
        }
    }

    public void loadResource(String s) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(s);
        if (is == null) {
            throw new Error("No such resource: " + s);
        }
        load(new InputStreamReader(is), s);
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

    public void emit(Root root) {
	m_root = root;

        for (Iterator it = m_root.getObjectTypes().iterator();
	     it.hasNext(); ) {
            m_symbols.addEmitted((ObjectType) it.next());
        }

        if (root.getObjectType("global.String") == null) {
            loadResource("com/arsdigita/persistence/proto/pdl/global.pdl");
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
                    if (type.hasProperty(name)) {
                        m_errors.fatal(prop, "duplicate property: " + name);
                        return null;
                    }

                    Role result =
                        new Role(prop.getName().getName(),
                                 m_symbols.getEmitted
                                 (m_symbols.lookup(prop.getType())),
                                 prop.isComponent(),
                                 prop.isCollection(),
                                 prop.isNullable());
                    type.addProperty(result);
                    m_properties.put(prop, result);
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
                        String rev = "~" + prop.getName().getName() +
                            type.getQualifiedName().replace('.', '$');
                        Role reverse = new Role(rev, type, true, true, true);
                        role.getType().addProperty(reverse);
                        role.setReverse(reverse);
                    }
                }

                public void onAssociation(AssociationNd assn) {
		    PropertyNd one = assn.getRoleOne();
		    PropertyNd two = assn.getRoleTwo();
		    Collection props = assn.getProperties();
		    ObjectType oneot =
			m_symbols.getEmitted(m_symbols.lookup(one.getType()));
		    ObjectType twoot =
			m_symbols.getEmitted(m_symbols.lookup(two.getType()));

		    if (props.size() > 0) {
			Role rone = new Role(one.getName().getName(), oneot,
					     one.isComponent(), false, false);
			Role rtwo = new Role(two.getName().getName(), twoot,
					     two.isComponent(), false, false);

			ObjectType ot = m_symbols.getEmitted(linkName(assn));

			ot.addProperty(rone);
			Role revOne = new Role("~" + rone.getName(), ot,
					       true, true, true);
			rone.getType().addProperty(revOne);
			rone.setReverse(revOne);

			ot.addProperty(rtwo);
			Role revTwo = new Role("~" + rtwo.getName(), ot,
					       true, true, true);
			rtwo.getType().addProperty(revTwo);
			rtwo.setReverse(revTwo);

			for (Iterator it = props.iterator(); it.hasNext(); ) {
			    PropertyNd prop = (PropertyNd) it.next();
			    define(ot, prop);
			}

			Link l = new Link(rone.getName(), rtwo, rone,
					  one.isCollection(),
					  one.isNullable());
			m_links.add(l);
			twoot.addProperty(l);
			l = new Link(rtwo.getName(), rone, rtwo,
				     two.isCollection(), two.isNullable());
			oneot.addProperty(l);
			m_links.add(l);
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
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            m_root.addObjectType(m_symbols.getEmitted(ot));
        }

        m_ast.traverse(new Node.Switch() {
                public void onObjectType(ObjectTypeNd otn) {
                    m_root.addObjectMap
                        (new ObjectMap(m_symbols.getEmitted(otn)));
                }
		public void onAssociation(AssociationNd assn) {
		    ObjectType ot = m_symbols.getEmitted(linkName(assn));
		    if (ot != null) {
			m_root.addObjectType(ot);
			m_root.addObjectMap(new ObjectMap(ot));
		    }
		}
            });

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

		    if (jcn == null || acn == null) {
			return;
		    }

		    Class javaClass;
		    try {
			javaClass = Class.forName(jcn.getName());
		    } catch (ClassNotFoundException e) {
			m_errors.fatal(jcn, e.getMessage());
			return;
		    }

		    ot.setJavaClass(javaClass);

		    try {
			Class adapterClass = Class.forName(acn.getName());
			Adapter ad = (Adapter) adapterClass.newInstance();
			Adapter.addAdapter(javaClass, ad);
		    } catch (IllegalAccessException e) {
			m_errors.fatal(acn, e.getMessage());
		    } catch (ClassNotFoundException e) {
			m_errors.fatal(acn, e.getMessage());
		    } catch (InstantiationException e) {
			m_errors.fatal(acn, e.getMessage());
		    }
		}
	    });

	m_errors.check();

        m_ast.traverse(new Node.Switch() {
            public void onProperty(PropertyNd pnd) {
                Node n = pnd.getMapping();
                if (!(n instanceof ColumnNd)) {
                    return;
                }

                ObjectType ot = m_symbols.getEmitted(pnd.getType());
                Column col = lookup((ColumnNd)n);
                if (col.getType() == Integer.MIN_VALUE) {
                    Adapter ad = Adapter.getAdapter(ot.getJavaClass());
                    col.setType(ad.defaultJDBCType());
                }
            }
        });

	propogateTypes();
    }

    public void emitVersioned() {
        Node.Switch nodeSwitch = VersioningMetadata.getVersioningMetadata().
            nodeSwitch(m_properties);
        m_ast.traverse(nodeSwitch);
    }

    private class UniqueTraversal extends Node.Traversal {

        private HashMap m_cols = new HashMap();
        private Node m_nd;
        private ArrayList m_ids = new ArrayList();
        private String m_id = null;
        private boolean m_primary;

        public UniqueTraversal(Node nd, Collection ids,
                               boolean primary) {
            m_nd = nd;
            m_primary = primary;
            for (Iterator it = ids.iterator(); it.hasNext(); ) {
                m_ids.add(((IdentifierNd) it.next()).getName());
            }
        }

        public boolean accept(Node child) {
            Node.Field f = child.getField();
            if (f == ObjectTypeNd.PROPERTIES) {
                PropertyNd p = (PropertyNd) child;
                m_id = p.getName().getName();
                return m_ids.contains(m_id);
            } else if (f == PropertyNd.MAPPING) {
                return true;
            } else if (f == JoinPathNd.JOINS) {
                return child.getIndex() == 0;
            } else if (f == JoinNd.FROM) {
                return true;
            } else {
                return false;
            }
        }

        public void onColumn(ColumnNd colnd) {
            m_cols.put(m_id, colnd);
        }

        public void emit() {
            if (m_cols.size() == 0) {
                m_errors.warn(m_nd, "no metadata");
                return;
            }

            Column[] cols = new Column[m_ids.size()];
            int index = 0;
            for (Iterator it = m_ids.iterator(); it.hasNext(); ) {
                String id = (String) it.next();
                ColumnNd colnd = (ColumnNd) m_cols.get(id);
                if (colnd == null) {
                    m_errors.warn(m_nd, "no metadata for " + id);
                    return;
                }
                cols[index++] = lookup(colnd);
            }
            unique(m_nd, cols, m_primary);
        }

    }

    private HashMap m_primaryKeys = new HashMap();

    private void unique(Node nd, Column[] cols, boolean primary) {
        Table table = cols[0].getTable();
        if (table.getUniqueKey(cols) != null) {
            m_errors.warn(nd, "duplicate key");
            return;
        }
        UniqueKey key = new UniqueKey(table, null, cols);
        if (primary) {
            UniqueKey pk = table.getPrimaryKey();
            if (pk != null) {
                Node prev = (Node) m_primaryKeys.get(pk);
                m_errors.warn
                    (nd, "table already has primary key: " +
                     prev.getLocation());
                if (prev instanceof ObjectKeyNd) {
                    return;
                }
            }
            table.setPrimaryKey(key);
            m_primaryKeys.put(key, nd);
	    m_root.setLocation(table, nd.getFile().getName(), nd.getLine(),
			       nd.getColumn());
        }
    }

    private void unique(Node nd, Collection ids, boolean primary) {
        UniqueTraversal ut = new UniqueTraversal(nd, ids, primary);
        nd.getParent().traverse(ut);
        ut.emit();
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
                    }

                    DbTypeNd type = colNd.getType();
                    Column col = table.getColumn(colNd.getName().getName());

                    if (col == null) {
                        if (type == null) {
                            col = new Column(colNd.getName().getName());
                        } else {
                            col = new Column
                                (colNd.getName().getName(), type.getType(),
                                 type.getSize(), type.getPrecision());
                        }
                        table.addColumn(col);
                    } else if (type != null) {
                        col.setType(type.getType());
                        col.setSize(type.getSize());
                        col.setPrecision(type.getPrecision());
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
                    unique(nd, nd.getProperties(), true);
                }

                public void onUniqueKey(UniqueKeyNd nd) {
                    unique(nd, nd.getProperties(), false);
                }

                public void onReferenceKey(ReferenceKeyNd nd) {
                    unique(nd, new Column[] { lookup(nd.getCol()) },
                           true);
                }

                public void onProperty(PropertyNd nd) {
                    if (nd.isUnique()) {
                        ArrayList ids = new ArrayList();
                        ids.add(nd.getName());
                        unique(nd, ids, false);
                    }
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, FileNd.ASSOCIATIONS,
                ObjectTypeNd.OBJECT_KEY, ObjectTypeNd.UNIQUE_KEYS,
                ObjectTypeNd.REFERENCE_KEY, ObjectTypeNd.PROPERTIES,
                AssociationNd.ROLE_ONE, AssociationNd.ROLE_TWO,
                AssociationNd.PROPERTIES
            }));

	m_ast.traverse(new Node.Switch() {
		public void onJoinPath(JoinPathNd jpn) {
		    List joins = jpn.getJoins();
		    if (joins.size() != 2) {
			return;
		    }

		    ColumnNd from = ((JoinNd) joins.get(0)).getTo();
		    ColumnNd to = ((JoinNd) joins.get(1)).getFrom();
		    unique(jpn, new Column[] { lookup(from), lookup(to) },
			   true);
		}
	    }, new Node.IncludeFilter(new Node.Field[] {
		AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.PROPERTIES,
		FileNd.ASSOCIATIONS, AssociationNd.PROPERTIES,
		AssociationNd.ROLE_ONE, PropertyNd.MAPPING
	    }));
    }

    private ObjectMap getMap(Node nd) {
        return m_root.getObjectMap
            (m_symbols.getEmitted((ObjectTypeNd) nd.getParent()));
    }

    private void emitMapping() {
        m_ast.traverse(new Node.Switch() {
                public void onIdentifier(IdentifierNd id) {
                    ObjectTypeNd ot =
                        (ObjectTypeNd) id.getParent().getParent();
                    ObjectMap om = m_root.getObjectMap
			(m_symbols.getEmitted(ot));
                    Role role = (Role) m_symbols.getEmitted(ot).getProperty
                        (id.getName());
                    om.getKeyProperties().add(role);
                    role.setNullable(false);
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.OBJECT_KEY,
                ObjectKeyNd.PROPERTIES
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
		    keys.add(ptwo);
		    keys.add(pone);

		    if (one.getMapping() != null) {
			emitMapping(pone, (JoinPathNd) one.getMapping(), 1, 2);
			emitMapping(pone.getReverse(),
				    (JoinPathNd) two.getMapping(), 0, 1);
		    } else {
			om.addMapping(new Static(Path.get(pone.getName())));
			m_root.getObjectMap(pone.getType()).addMapping
			    (new Static
			     (Path.get(pone.getReverse().getName())));
		    }


		    if (two.getMapping() != null) {
			emitMapping(ptwo, (JoinPathNd) two.getMapping(), 1, 2);
			emitMapping(ptwo.getReverse(),
				    (JoinPathNd) one.getMapping(), 0, 1);
		    } else {
			om.addMapping(new Static(Path.get(ptwo.getName())));
			m_root.getObjectMap(ptwo.getType()).addMapping
			    (new Static
			     (Path.get(ptwo.getReverse().getName())));
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

	for (Iterator it = m_links.iterator(); it.hasNext(); ) {
	    Link l = (Link) it.next();
	    ObjectMap om = m_root.getObjectMap(l.getContainer());
	    om.addMapping(new Static(Path.get(l.getName())));
	}

        m_ast.traverse(new Node.Switch() {
                public void onProperty(PropertyNd pn) {
                    Role prop = (Role) m_properties.get(pn);
                    if (prop == null) { return; }

                    ObjectMap om = m_root.getObjectMap(prop.getContainer());

                    Object mapping = pn.getMapping();
                    if (mapping == null) {
                        om.addMapping(new Static(Path.get(prop.getName())));
                    } else if (mapping instanceof ColumnNd) {
                        emitMapping(prop, (ColumnNd) mapping);
                    } else {
                        emitMapping(prop, (JoinPathNd) mapping);
                    }

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
            });

        m_ast.traverse(new Node.Switch() {
                public void onReferenceKey(ReferenceKeyNd rkn) {
                    Column key = lookup(rkn.getCol());
                    ObjectMap om = getMap(rkn);
                    om.setTable(key.getTable());
                }

                public void onObjectKey(ObjectKeyNd okn) {
                    ObjectMap om = getMap(okn);
                    IdentifierNd prop =
                        (IdentifierNd) okn.getProperties().iterator().next();
                    Mapping m = om.getMapping(Path.get(prop.getName()));
                    if (m != null) {
                        om.setTable(m.getTable());
                    }
                }
            });

	m_ast.traverse(new Node.Switch() {
		public void onReferenceKey(ReferenceKeyNd rkn) {
		    Column key = lookup(rkn.getCol());
		    ObjectMap om = getMap(rkn);
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
				("unable to find supertable");
			}
			fk(cols, table.getPrimaryKey());
		    }
		}
	    });

/*        m_ast.traverse(new Node.Switch() {
                public void onJoin(JoinNd nd) {
                    ObjectMap om = m_root.getObjectMap
                        (m_symbols.getEmitted
                         ((ObjectTypeNd) nd.getParent().getParent()));
                    om.addJoin(new Join(lookup(nd.getFrom()),
                                        lookup(nd.getTo())));
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.JOIN_PATHS,
                JoinPathNd.JOINS
                }));*/

        m_ast.traverse(new Node.Switch() {
                public void onIdentifier(IdentifierNd nd) {
                    ObjectMap om = m_root.getObjectMap
                        (m_symbols.getEmitted
                         ((ObjectTypeNd) nd.getParent().getParent()
                          .getParent()));
                    om.addFetchedPath(Path.get(nd.getName()));
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.AGGRESSIVE_LOAD,
                AggressiveLoadNd.PATHS, PathNd.PATH
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
			cols[i].setPrecision(to[i].getPrecision());
			it.remove();
		    }
		}
	    }
	} while (m_fks.size() < before);
    }

    private void emitMapping(Property prop, ColumnNd colNd) {
	if (prop.getType().isKeyed()) {
	    m_errors.fatal(colNd, "association requires a join path");
	}

        ObjectMap om = m_root.getObjectMap(prop.getContainer());
        Value m = new Value(Path.get(prop.getName()), lookup(colNd));
        om.addMapping(m);
        Column col = lookup(colNd);
        if (prop.isNullable()) { col.setNullable(true); }
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
        if (fk != null) {
            return fk;
        }

        UniqueKey uk = to.getTable().getUniqueKey(new Column[] {to});
        if (uk == null) {
            m_errors.warn(tond, "not a unique key");
            return null;
        }

	return fk(new Column[] {from}, uk);
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
	emitMapping(prop, jpn, false);
    }

    private void emitMapping(Role prop, JoinPathNd jpn, boolean reverse) {
        if (reverse) {
            emitMapping(prop, jpn, jpn.getJoins().size(), 0);
        } else {
            emitMapping(prop, jpn, 0, jpn.getJoins().size());
        }
    }

    private void emitMapping(Role prop, JoinPathNd jpn, int start,
			     int stop) {
	if (!prop.getType().isKeyed()) {
	    m_errors.fatal(jpn, "cannot associate to a non keyed type");
	}

        ObjectMap om = m_root.getObjectMap(prop.getContainer());
        Path path = Path.get(prop.getName());
        List joins = jpn.getJoins();
        Mapping m;

        int magnitude = Math.abs(stop - start);
        boolean forward = stop > start;
        int low = forward ? start : stop;
        boolean joinForward;

        if (magnitude == 1) {
            JoinNd jn = (JoinNd) joins.get(low);
            if (lookup(jn.getTo()).isUniqueKey()) {
                joinForward = true;
            } else if (lookup(jn.getFrom()).isUniqueKey()) {
                joinForward = false;
            } else {
                m_errors.fatal(jpn, "neither end unique");
                return;
            }

            ForeignKey fk = fk(jn, joinForward);

            if (forward == joinForward) {
                m = new JoinTo(path, fk);
                setNullable(fk, prop.isNullable());
            } else {
                m = new JoinFrom(path, fk);
                if (!prop.isReversable()) {
                    setNullable(fk, prop.isNullable());
                }
            }
        } else if (magnitude == 2) {
            JoinNd first = (JoinNd) joins.get(low);
            JoinNd second = (JoinNd) joins.get(low + 1);
            ForeignKey from = fk(first, !forward);
            ForeignKey to = fk(second, forward);

	    // XXX: The not null checks here seem to be solely for the
	    // sake of the SelfReference.pdl test. We may want to
	    // alter the test and make it be an error for fk to return
	    // null.
	    if (from == null || to == null) {
		m_errors.warn(jpn, "unable to construct join through");
		return;
	    }

            if (forward) {
                m = new JoinThrough(path, from, to);
            } else {
                m = new JoinThrough(path, to, from);
            }
	    setNullable(from, false);
	    setNullable(to, false);
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

	// XXX: should add duplicate event checking here
	addEvent(m_root.getObjectMap(ot), ev, type, name);
    }

    private Collection getSQLBlocks(ObjectType ot, EventNd.Type type,
				    String role) {
        return getSQLBlocks(ot.getRoot().getObjectMap(ot), type, role);
    }


    private void setSQLBlocks(ObjectType ot, EventNd.Type type, String role,
                              Collection blocks) {
        setSQLBlocks(ot.getRoot().getObjectMap(ot), type, role, blocks);
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

    private void setSQLBlocks(ObjectMap om, EventNd.Type type, String role,
                              Collection blocks) {
        if (type.equals(EventNd.INSERT)) {
            om.setDeclaredInserts(blocks);
        } else if (type.equals(EventNd.UPDATE)) {
            om.setDeclaredUpdates(blocks);
        } else if (type.equals(EventNd.DELETE)) {
            om.setDeclaredDeletes(blocks);
        } else if (type.equals(EventNd.RETRIEVE)) {
            if (role == null) {
                om.setDeclaredRetrieves
                    (add(blocks, om.getDeclaredRetrieves()));
            } else {
                throw new Error("single block event");
            }
        } else if (type.equals(EventNd.ADD)) {
            getMapping(om, role).setAdds(blocks);
        } else if (type.equals(EventNd.REMOVE)) {
            getMapping(om, role).setRemoves(blocks);
        } else if (type.equals(EventNd.CLEAR)) {
            // do nothing
        } else if (type.equals(EventNd.RETRIEVE_ATTRIBUTES)) {
            om.setDeclaredRetrieves(add(om.getDeclaredRetrieves(), blocks));
        } else {
            throw new IllegalArgumentException("bad event type: " + type);
        }
    }

    private void addEvent(ObjectMap om, EventNd ev, EventNd.Type type,
			  String role) {
        if (type.equals(EventNd.RETRIEVE) &&
            role != null) {
            Mapping m = getMapping(om, role);
            m.setRetrieve(getBlock(ev));
            return;
        } else if (type.equals(EventNd.RETRIEVE_ALL)) {
            om.setRetrieveAll(getBlock(ev));
            return;
        }

        ArrayList blocks = new ArrayList();

        for (Iterator it = ev.getSQL().iterator(); it.hasNext(); ) {
            SQLBlockNd nd = (SQLBlockNd) it.next();
            blocks.add(getBlock(nd));
        }

        setSQLBlocks(om, type, role, blocks);
    }

    private SQLBlock getBlock(EventNd ev) {
        if (ev.getSQL().size() > 1) {
            m_errors.fatal(ev, "more than one sql block");
        }
        SQLBlockNd nd;
        Iterator it = ev.getSQL().iterator();
        if (it.hasNext()) {
            return getBlock((SQLBlockNd) it.next());
        } else {
            return null;
        }
    }

    private SQLBlock getBlock(SQLBlockNd nd) {
            try {
                SQLParser p = new SQLParser(new StringReader(nd.getSQL()));
                p.sql();
                final SQLBlock block = new SQLBlock(p.getSQL());
                for (Iterator ii = p.getAssigns().iterator(); ii.hasNext(); ) {
                    SQLParser.Assign assn = (SQLParser.Assign) ii.next();
                    block.addAssign(assn.getBegin(), assn.getEnd().getNext());
                }

                nd.traverse(new Node.Switch() {
                        public void onMapping(MappingNd nd) {
                            Path col = nd.getCol().getPath();
                            col = Path.get(col.getName());
                            block.addMapping(nd.getPath().getPath(), col);
                        }

                        public void onBinding(BindingNd nd) {
                            block.addType(nd.getPath().getPath(),
                                          nd.getType().getType());
                        }
                    });
		m_root.setLocation(block, nd.getFile().getName(), nd.getLine(),
				   nd.getColumn());
                return block;
            } catch (com.arsdigita.persistence.proto.common.ParseException e) {
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
                        (new DataOperation(name, getBlock(nd.getSQL())));
                }
            });
    }

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        for (int i = 0; i < args.length; i++) {
            pdl.load(new FileReader(args[i]), args[i]);
        }
        pdl.emit(Root.getRoot());
    }

}
