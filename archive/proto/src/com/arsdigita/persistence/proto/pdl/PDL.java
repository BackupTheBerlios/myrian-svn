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
 * @version $Revision: #32 $ $Date: 2003/03/18 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDL.java#32 $ by $Author: rhs $, $DateTime: 2003/03/18 15:44:06 $";
    private final static Logger LOG = Logger.getLogger(PDL.class);

    private AST m_ast = new AST();
    private ErrorReport m_errors = new ErrorReport();
    private SymbolTable m_symbols = new SymbolTable(m_errors);
    private HashMap m_properties = new HashMap();

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

    public void emit(final Root root) {
        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            m_symbols.addEmitted((ObjectType) it.next());
        }

        boolean loadingGlobal = false;

        if (root.getObjectType("global.String") == null) {
            loadResource("com/arsdigita/persistence/proto/pdl/global.pdl");
            loadingGlobal = true;
        }

        m_ast.traverse(new Node.Switch() {
                public void onObjectType(ObjectTypeNd ot) {
                    if (ot.hasReturns()) {
                        m_errors.warn(ot, "returns clause is deprecated");
                    }
                    m_symbols.define(ot);
                }
            });
        m_ast.traverse(new Node.Switch() {
                public void onType(TypeNd t) {
                    m_symbols.resolve(t);
                }
            });

        m_symbols.sort();
        m_symbols.emit();

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
                    define(m_symbols.getEmitted
                           ((ObjectTypeNd) prop.getParent()), prop);
                }

                public void onAssociation(AssociationNd assn) {
                    Role one =
                        define(m_symbols.getEmitted
                               (m_symbols.lookup(assn.getRoleOne().getType())),
                               assn.getRoleTwo());
                    Role two =
                        define(m_symbols.getEmitted
                               (m_symbols.lookup(assn.getRoleTwo().getType())),
                               assn.getRoleOne());
                    if (one != null && two != null) {
                        one.setReverse(two);
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
            root.addObjectType(m_symbols.getEmitted(ot));
        }

        m_ast.traverse(new Node.Switch() {
                public void onObjectType(ObjectTypeNd otn) {
                    root.addObjectMap
                        (new ObjectMap(m_symbols.getEmitted(otn)));
                }
            });

        emitDDL(root);

        m_errors.check();

        emitMapping(root);

        m_errors.check();

        emitEvents(root);

        m_errors.check();

        emitDataOperations(root);

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

		    try{
			Class adapterClass = Class.forName(acn.getName());
			Adapter ad = (Adapter) adapterClass.newInstance();
			Adapter.addAdapter(javaClass, ot, ad);
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
    }

    public void emitVersioned() {
        m_ast.traverse(VersioningMetadata.getVersioningMetadata().nodeSwitch());
    }

    private static interface Binder {
        void bind(PreparedStatement ps, int index, Object obj, int type)
            throws SQLException;
        Object fetch(ResultSet rs, String column) throws SQLException;
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

        public void emit(Root root) {
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
                cols[index++] = lookup(root, colnd);
            }
            unique(m_nd, cols, m_primary);
        }

    }

    private void unique(Node nd, Column[] cols, boolean primary) {
        Table table = cols[0].getTable();
        if (table.getUniqueKey(cols) != null) {
            m_errors.warn(nd, "duplicate key");
            return;
        }
        UniqueKey key = new UniqueKey(table, null, cols);
        if (primary) {
            table.setPrimaryKey(key);
        }
    }

    private void unique(Root root, Node nd, Collection ids, boolean primary) {
        UniqueTraversal ut = new UniqueTraversal(nd, ids, primary);
        nd.getParent().traverse(ut);
        ut.emit(root);
    }

    private void emitDDL(final Root root) {
        final HashMap tables = new HashMap();
        for (Iterator it = root.getTables().iterator(); it.hasNext(); ) {
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
                root.addTable(table);
            }
        }

        m_ast.traverse(new Node.Switch() {
                public void onObjectKey(ObjectKeyNd nd) {
                    unique(root, nd, nd.getProperties(), true);
                }

                public void onUniqueKey(UniqueKeyNd nd) {
                    unique(root, nd, nd.getProperties(), false);
                }

                public void onReferenceKey(ReferenceKeyNd nd) {
                    unique(nd, new Column[] { lookup(root, nd.getCol()) },
                           true);
                }

                public void onProperty(PropertyNd nd) {
                    if (nd.isUnique()) {
                        ArrayList ids = new ArrayList();
                        ids.add(nd.getName());
                        unique(root, nd, ids, false);
                    }
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, FileNd.ASSOCIATIONS,
                ObjectTypeNd.OBJECT_KEY, ObjectTypeNd.UNIQUE_KEYS,
                ObjectTypeNd.REFERENCE_KEY, ObjectTypeNd.PROPERTIES,
                AssociationNd.ROLE_ONE, AssociationNd.ROLE_TWO,
                AssociationNd.PROPERTIES
            }));
    }

    private ObjectMap getMap(Root root, Node nd) {
        return root.getObjectMap
            (m_symbols.getEmitted((ObjectTypeNd) nd.getParent()));
    }

    private void emitMapping(final Root root) {
        m_ast.traverse(new Node.Switch() {
                public void onIdentifier(IdentifierNd id) {
                    ObjectTypeNd ot =
                        (ObjectTypeNd) id.getParent().getParent();
                    ObjectMap om = root.getObjectMap(m_symbols.getEmitted(ot));
                    om.getKeyProperties().add
                        (m_symbols.getEmitted(ot).getProperty(id.getName()));
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.OBJECT_KEY,
                ObjectKeyNd.PROPERTIES
            }));

        m_ast.traverse(new Node.Switch() {
                public void onProperty(PropertyNd pn) {
                    Property prop = (Property) m_properties.get(pn);
                    if (prop == null) { return; }

                    ObjectMap om = root.getObjectMap(prop.getContainer());

                    Object mapping = pn.getMapping();
                    if (mapping == null) {
                        om.addMapping(new Static(Path.get(prop.getName())));
                    } else if (mapping instanceof ColumnNd) {
                        emitMapping(root, prop, (ColumnNd) mapping);
                    } else {
                        emitMapping(root, prop, (JoinPathNd) mapping);
                    }
                }
            });

        m_ast.traverse(new Node.Switch() {
                public void onReferenceKey(ReferenceKeyNd rkn) {
                    Column key = lookup(root, rkn.getCol());
                    ObjectMap om = getMap(root, rkn);
                    om.setTable(key.getTable());
                }

                public void onObjectKey(ObjectKeyNd okn) {
                    ObjectMap om = getMap(root, okn);
                    IdentifierNd prop =
                        (IdentifierNd) okn.getProperties().iterator().next();
                    Mapping m = om.getMapping(Path.get(prop.getName()));
                    if (m != null) {
                        om.setTable(m.getTable());
                    }
                }
            });

/*        m_ast.traverse(new Node.Switch() {
                public void onJoin(JoinNd nd) {
                    ObjectMap om = root.getObjectMap
                        (m_symbols.getEmitted
                         ((ObjectTypeNd) nd.getParent().getParent()));
                    om.addJoin(new Join(lookup(root, nd.getFrom()),
                                        lookup(root, nd.getTo())));
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.JOIN_PATHS,
                JoinPathNd.JOINS
                }));*/

        m_ast.traverse(new Node.Switch() {
                public void onIdentifier(IdentifierNd nd) {
                    ObjectMap om = root.getObjectMap
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

    private void emitMapping(Root root, Property prop, ColumnNd colNd) {
        ObjectMap om = root.getObjectMap(prop.getContainer());
        Value m = new Value(Path.get(prop.getName()), lookup(root, colNd));
        om.addMapping(m);
    }

    private ForeignKey fk(Root root, JoinNd jn, boolean forward) {
        ColumnNd fromnd;
        ColumnNd tond;

        if (forward) {
            fromnd = jn.getFrom();
            tond = jn.getTo();
        } else {
            fromnd = jn.getTo();
            tond = jn.getFrom();
        }

        Column from = lookup(root, fromnd);
        Column to = lookup(root, tond);

        ForeignKey fk = from.getTable().getForeignKey(new Column[] {from});
        if (fk != null) {
            return fk;
        }

        UniqueKey uk = to.getTable().getUniqueKey(new Column[] {to});
        if (uk == null) {
            m_errors.warn(tond, "not a unique key");
            return null;
        }
        return new ForeignKey
            (from.getTable(), null, new Column[] {from}, uk, false);
    }

    private void emitMapping(Root root, Property prop, JoinPathNd jpn) {
        ObjectMap om = root.getObjectMap(prop.getContainer());
        Path path = Path.get(prop.getName());
        List joins = jpn.getJoins();
        Mapping m;
        if (joins.size() == 1) {
            JoinNd jn = (JoinNd) joins.get(0);
            if (lookup(root, jn.getTo()).isUniqueKey()) {
                ForeignKey fk = fk(root, jn, true);
                m = new JoinTo(path, fk);
            } else if (lookup(root, jn.getFrom()).isUniqueKey()) {
                ForeignKey fk = fk(root, jn, false);
                m = new JoinFrom(path, fk);
            } else {
                m_errors.fatal(jpn, "neither end unique");
                return;
            }
        } else if (joins.size() == 2) {
            JoinNd first = (JoinNd) joins.get(0);
            JoinNd second = (JoinNd) joins.get(1);
            ForeignKey from = fk(root, first, false);
            ForeignKey to = fk(root, second, true);
            m = new JoinThrough(path, from, to);
        } else {
            m_errors.fatal(jpn, "bad join path");
            return;
        }

        om.addMapping(m);
    }

    private Column lookup(Root root, ColumnNd colNd) {
        Table table = root.getTable(colNd.getTable().getName());
        return table.getColumn(colNd.getName().getName());
    }

    private void emitEvents(final Root root) {
        m_ast.traverse(new Node.Switch() {
                public void onEvent(EventNd nd) {
                    addEvent(root, nd);
                }
            });
    }

    private void addEvent(Root root, EventNd ev) {
        if (ev.getParent() instanceof ObjectTypeNd) {
            ObjectTypeNd otn = (ObjectTypeNd) ev.getParent();
            addEvent(m_symbols.getEmitted(otn), ev, ev.getName());
        } else {
            AssociationNd assn = (AssociationNd) ev.getParent();
            String one = assn.getRoleOne().getName().getName();
            String two = assn.getRoleTwo().getName().getName();
            ObjectType oneType =
                m_symbols.getEmitted(assn.getRoleOne().getType());
            ObjectType twoType =
                m_symbols.getEmitted(assn.getRoleTwo().getType());

            if (ev.getName() == null) {
                addEvent(twoType, ev, one);
            } else if (ev.getName().getName().equals(one)) {
                if (!ev.isSingle()) {
                    Collection blocks = getSQLBlocks(oneType, ev, two);
                    if (blocks != null && blocks.size() > 0) {
                        m_errors.warn
                            (ev, "both ends of a two way specified, " +
                             "ignoring this event");
                        return;
                    }
                }
                addEvent(twoType, ev, one);
            } else if (ev.getName().getName().equals(two)) {
                if (!ev.isSingle()) {
                    Collection blocks = getSQLBlocks(twoType, ev, one);
                    if (blocks != null && blocks.size() > 0) {
                        m_errors.warn
                            (ev, "both ends of a two way specified, " +
                             "ignoring this event");
                        return;
                    }
                }
                addEvent(oneType, ev, two);
            } else {
                m_errors.warn
                    (ev.getName(), "no such role: " + ev.getName().getName());
            }
        }
    }

    private void addEvent(ObjectType ot, EventNd ev, IdentifierNd role) {
        addEvent(ot, ev, role == null ? null : role.getName());
    }

    private void addEvent(ObjectType ot, EventNd ev, String role) {
        addEvent(ot.getRoot().getObjectMap(ot), ev, role);
    }

    private Collection getSQLBlocks(ObjectType ot, EventNd ev, String role) {
        return getSQLBlocks(ot.getRoot().getObjectMap(ot), ev, role);
    }

    private Collection getSQLBlocks(ObjectMap om, EventNd ev, String role) {
        Collection blocks;
        if (ev.getType().equals(EventNd.INSERT)) {
            blocks = om.getDeclaredInserts();
        } else if (ev.getType().equals(EventNd.UPDATE)) {
            blocks = om.getDeclaredUpdates();
        } else if (ev.getType().equals(EventNd.DELETE)) {
            blocks = om.getDeclaredDeletes();
        } else if (ev.getType().equals(EventNd.RETRIEVE)) {
            if (role == null) {
                blocks = om.getDeclaredRetrieves();
            } else {
                throw new Error("single block event");
            }
        } else if (ev.getType().equals(EventNd.ADD)) {
            blocks = getMapping(om, role).getAdds();
        } else if (ev.getType().equals(EventNd.REMOVE)) {
            blocks = getMapping(om, role).getRemoves();
        } else if (ev.getType().equals(EventNd.CLEAR)) {
            blocks = new ArrayList();
        } else if (ev.getType().equals(EventNd.RETRIEVE_ATTRIBUTES)) {
            blocks = new ArrayList();
        } else {
            m_errors.fatal(ev, "bad event type: " + ev.getType());
            blocks = new ArrayList();
        }

        return blocks;
    }

    private void setSQLBlocks(ObjectType ot, EventNd ev, String role,
                              Collection blocks) {
        setSQLBlocks(ot.getRoot().getObjectMap(ot), ev, role, blocks);
    }

    private void setSQLBlocks(ObjectMap om, EventNd ev, String role,
                              Collection blocks) {
        if (ev.getType().equals(EventNd.INSERT)) {
            om.setDeclaredInserts(blocks);
        } else if (ev.getType().equals(EventNd.UPDATE)) {
            om.setDeclaredUpdates(blocks);
        } else if (ev.getType().equals(EventNd.DELETE)) {
            om.setDeclaredDeletes(blocks);
        } else if (ev.getType().equals(EventNd.RETRIEVE)) {
            if (role == null) {
                om.setDeclaredRetrieves(blocks);
            } else {
                throw new Error("single block event");
            }
        } else if (ev.getType().equals(EventNd.ADD)) {
            getMapping(om, role).setAdds(blocks);
        } else if (ev.getType().equals(EventNd.REMOVE)) {
            getMapping(om, role).setRemoves(blocks);
        } else if (ev.getType().equals(EventNd.CLEAR)) {
            // do nothing
        } else if (ev.getType().equals(EventNd.RETRIEVE_ATTRIBUTES)) {
            // do nothing
        } else {
            m_errors.fatal(ev, "bad event type: " + ev.getType());
            // do nothing
        }
    }

    private void addEvent(ObjectMap om, EventNd ev, String role) {
        if (ev.getType().equals(EventNd.RETRIEVE) &&
            role != null) {
            Mapping m = getMapping(om, role);
            m.setRetrieve(getBlock(ev));
            return;
        } else if (ev.getType().equals(EventNd.RETRIEVE_ALL)) {
            om.setRetrieveAll(getBlock(ev));
            return;
        }

        ArrayList blocks = new ArrayList();

        for (Iterator it = ev.getSQL().iterator(); it.hasNext(); ) {
            SQLBlockNd nd = (SQLBlockNd) it.next();
            blocks.add(getBlock(nd));
        }

        setSQLBlocks(om, ev, role, blocks);
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
                return block;
            } catch (com.arsdigita.persistence.proto.common.ParseException e) {
                m_errors.fatal(nd, e.getMessage());
                return null;
            }
    }

    private Mapping getMapping(ObjectMap om, String role) {
        Path path = Path.get(role);
        Mapping m = om.getMapping(path);
        return m;
    }

    private void emitDataOperations(final Root root) {
        m_ast.traverse(new Node.Switch() {
                public void onDataOperation(DataOperationNd nd) {
                    Path name = Path.get(nd.getFile().getModel().getName() +
                                         "." + nd.getName().getName());
                    root.addDataOperation
                        (new DataOperation(name, getBlock(nd.getSQL())));
                }
            });
    }

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        for (int i = 0; i < args.length; i++) {
            pdl.load(new FileReader(args[i]), args[i]);
        }
        Root root = Root.getRoot();
        pdl.emit(root);
    }

}
