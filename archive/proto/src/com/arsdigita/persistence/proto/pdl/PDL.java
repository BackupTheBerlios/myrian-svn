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
 * @version $Revision: #23 $ $Date: 2003/02/27 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDL.java#23 $ by $Author: rhs $, $DateTime: 2003/02/27 11:01:00 $";
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

    private static class SimpleAdapter extends Adapter {

        private ObjectType m_type;
        private Binder m_binder;

        SimpleAdapter(ObjectType type, Binder binder) {
            if (type == null) { throw new IllegalArgumentException(); }
            m_type = type;
            m_binder = binder;
        }

        public Object fetch(ResultSet rs, String column) throws SQLException {
            return m_binder.fetch(rs, column);
        }

        public void bind(PreparedStatement ps, int index, Object obj,
                         int type) throws SQLException {
            m_binder.bind(ps, index, obj, type);
        }

        public PropertyMap getProperties(Object obj) {
            return new PropertyMap(m_type);
        }

        public ObjectType getObjectType(Object obj) { return m_type; }
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

        emitDDL(root);

        m_errors.check();

        emitMapping(root);

        m_errors.check();

        emitConstraints(root);

        m_errors.check();

        emitEvents(root);

        m_errors.check();

        if (loadingGlobal) {
            String[] types = new String[] {
                "global.Integer",
                "global.BigInteger",
                "global.BigDecimal",
                "global.String",
                "global.Float",
                "global.Date",
                "global.Boolean",
                "global.Blob",
                "global.Byte",
                "global.Character",
                "global.Double",
                "global.Short",
                "global.Long",
                "global.Float"
            };
            Class[] classes = new Class[] {
                Integer.class,
                java.math.BigInteger.class,
                java.math.BigDecimal.class,
                String.class,
                Float.class,
                java.util.Date.class,
                Boolean.class,
                byte[].class,
                Byte.class,
                Character.class,
                Double.class,
                Short.class,
                Long.class,
                Float.class
            };
            Binder[] binders = new Binder[] {
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setInt(index, ((Integer) obj).intValue());
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            int i = rs.getInt(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Integer(i);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBigDecimal
                                (index, new BigDecimal((BigInteger) obj));
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            BigDecimal bd = rs.getBigDecimal(column);
                            if (bd == null) {
                                return null;
                            } else {
                                return bd.toBigInteger();
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBigDecimal(index, (BigDecimal) obj);
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            return rs.getBigDecimal(column);
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setString(index, (String) obj);
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            return rs.getString(column);
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setFloat(index, ((Float) obj).floatValue());
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            float f = rs.getFloat(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Float(f);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            Timestamp tstamp = new Timestamp
                                (((java.util.Date) obj).getTime());
                            ps.setTimestamp(index, tstamp);
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            Timestamp tstamp = rs.getTimestamp(column);
                            if (tstamp == null) {
                                return null;
                            } else {
                                return new java.util.Date(tstamp.getTime());
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBoolean
                                (index, ((Boolean) obj).booleanValue());
                        }
                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            boolean bool = rs.getBoolean(column);
                            if (rs.wasNull()) {
                                return null;
                            } else if (bool) {
                                return Boolean.TRUE;
                            } else {
                                return Boolean.FALSE;
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            byte[] bytes = (byte[]) obj;
                            ps.setBytes(index, bytes);
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            return rs.getBytes(column);
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setByte(index, ((Byte) obj).byteValue());
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            byte b = rs.getByte(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Byte(b);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            Character charObj = (Character)obj;
                            if (charObj == null ||
                                "".equals(charObj.toString())) {
                                ps.setString(index, null);
                            } else {
                                ps.setString(index, charObj.toString());
                            }
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            String str = rs.getString(column);
                            if (str != null && str.length() > 0) {
                                return new Character(str.charAt(0));
                            } else {
                                return null;
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setDouble(index, ((Double) obj).doubleValue());
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            double d = rs.getDouble(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Double(d);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setShort(index, ((Short) obj).shortValue());
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            short s = rs.getShort(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Short(s);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setLong(index, ((Long) obj).longValue());
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            long l = rs.getLong(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Long(l);
                            }
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setFloat(index, ((Float) obj).floatValue());
                        }

                        public Object fetch(ResultSet rs, String column)
                            throws SQLException {
                            float f = rs.getFloat(column);
                            if (rs.wasNull()) {
                                return null;
                            } else {
                                return new Float(f);
                            }
                        }
                    }
            };

            for (int i = 0; i < types.length; i++) {
                ObjectType type = root.getObjectType(types[i]);
                Adapter.addAdapter(classes[i], type,
                                   new SimpleAdapter(type, binders[i]));
            }
        }
    }

    public void emitVersioned() {
        m_ast.traverse(VersioningMetadata.getVersioningMetadata().nodeSwitch());
    }

    private static interface Binder {
        void bind(PreparedStatement ps, int index, Object obj, int type)
            throws SQLException;
        Object fetch(ResultSet rs, String column) throws SQLException;
    }

    private void emitDDL(Root root) {
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
    }

    private void unique(ObjectMap map, Node nd, Collection ids,
                        boolean primary) {
        final ArrayList cols = new ArrayList();
        for (Iterator it = ids.iterator(); it.hasNext(); ) {
            IdentifierNd id = (IdentifierNd) it.next();
            Mapping m = map.getMapping(Path.get(id.getName()));
            // XXX: no metadata
            if (m == null) {
                return;
            }
            m.dispatch(new Mapping.Switch() {
                    public void onValue(ValueMapping vm) {
                        cols.add(vm.getColumn());
                    }

                    public void onReference(ReferenceMapping rm) {
                        if (!rm.isJoinTo()) {
                            throw new Error("can't do this");
                        }

                        cols.add(rm.getJoin(0).getFrom());
                    }

                    public void onStatic(StaticMapping sm) {
                        throw new Error("can't do this");
                    }
                });
        }

        unique(nd, (Column[]) cols.toArray(new Column[0]), primary);
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

    private void emitConstraints(final Root root) {
        m_ast.traverse(new Node.Switch() {
                private ObjectMap getMap(Node nd) {
                    return root.getObjectMap
                        (m_symbols.getEmitted((ObjectTypeNd) nd.getParent()));
                }

                public void onObjectKey(ObjectKeyNd nd) {
                    unique(getMap(nd), nd, nd.getProperties(), true);
                }

                public void onUniqueKey(UniqueKeyNd nd) {
                    unique(getMap(nd), nd, nd.getProperties(), false);
                }

                public void onReferenceKey(ReferenceKeyNd nd) {
                    unique(nd, new Column[] { lookup(root, nd.getCol()) },
                           true);
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, FileNd.OBJECT_TYPES, ObjectTypeNd.OBJECT_KEY,
                ObjectTypeNd.UNIQUE_KEYS, ObjectTypeNd.REFERENCE_KEY
            }));
    }

    private void emitMapping(final Root root) {
        m_ast.traverse(new Node.Switch() {
                public void onObjectType(ObjectTypeNd otn) {
                    root.addObjectMap
                        (new ObjectMap(m_symbols.getEmitted(otn)));
                }
            });

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
                    Object mapping = pn.getMapping();
                    if (mapping == null) {
                        return;
                    }
                    if (mapping instanceof ColumnNd) {
                        emitMapping(root, prop, (ColumnNd) mapping);
                    } else {
                        emitMapping(root, prop, (JoinPathNd) mapping);
                    }
                }
            });

        for (Iterator it = m_symbols.getObjectTypes().iterator();
             it.hasNext(); ) {
            ObjectTypeNd otn = (ObjectTypeNd) it.next();
            ReferenceKeyNd rkn = otn.getReferenceKey();
            if (rkn != null) {
                Column from = lookup(root, rkn.getCol());
                Column to;

                ObjectType ot = m_symbols.getEmitted(otn);
                ObjectType sup = ot.getSupertype();
                ObjectMap om = root.getObjectMap(ot);
                ObjectMap supm = root.getObjectMap(sup);
                if (supm.getSuperJoin() == null) {
                    Property prop =
                        (Property) supm.getKeyProperties().iterator().next();
                    Mapping m = supm.getMapping(Path.get(prop.getName()));
                    if (m.isValue()) {
                        to = ((ValueMapping) m).getColumn();
                    } else {
                        to = ((Join) ((ReferenceMapping) m)
                              .getJoins().iterator().next()).getFrom();
                    }
                } else {
                    to = supm.getSuperJoin().getFrom();
                }

                om.setSuperJoin(new Join(from, to));
            }
        }

        m_ast.traverse(new Node.Switch() {
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
            }));

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
        ValueMapping vm = new ValueMapping(Path.get(prop.getName()),
                                           lookup(root, colNd));
        om.addMapping(vm);
    }

    private void emitMapping(Root root, Property prop, JoinPathNd jpn) {
        ObjectMap om = root.getObjectMap(prop.getContainer());
        ReferenceMapping rm = new ReferenceMapping(Path.get(prop.getName()));
        for (Iterator it = jpn.getJoins().iterator(); it.hasNext(); ) {
            JoinNd jn = (JoinNd) it.next();
            rm.addJoin(new Join(lookup(root, jn.getFrom()),
                                lookup(root, jn.getTo())));
        }
        om.addMapping(rm);
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
                    if (blocks.size() > 0) {
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
                    if (blocks.size() > 0) {
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

        Collection blocks = getSQLBlocks(om, ev, role);

        for (Iterator it = ev.getSQL().iterator(); it.hasNext(); ) {
            SQLBlockNd nd = (SQLBlockNd) it.next();
            blocks.add(getBlock(nd));
        }
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
                for (Iterator ii = p.getBindings().iterator();
                     ii.hasNext(); ) {
                    Path path = Path.get((String) ii.next());
                    block.addBinding(path);
                }
                for (Iterator ii = p.getAssigns().iterator(); ii.hasNext(); ) {
                    SQLParser.Assign assn = (SQLParser.Assign) ii.next();
                    SQLBlock.Assign bassn = block.addAssign
                        (assn.getBegin(), assn.getEnd());
                    for (Iterator iter = assn.getBindings().iterator();
                         iter.hasNext(); ) {
                        Path path = Path.get((String) iter.next());
                        bassn.addBinding(path);
                    }
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
            } catch (ParseException e) {
                m_errors.fatal(nd, e.getMessage());
                return null;
            }
    }

    private Mapping getMapping(ObjectMap om, String role) {
        Path path = Path.get(role);
        Mapping m = om.getMapping(path);
        if (m == null) {
            m = new StaticMapping(path);
            om.addMapping(m);
        }
        return m;
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
