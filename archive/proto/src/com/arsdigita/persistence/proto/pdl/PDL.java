package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.Adapter;
import com.arsdigita.persistence.proto.PropertyMap;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.pdl.nodes.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.io.Reader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.math.*;
import java.sql.*;

/**
 * PDL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #16 $ $Date: 2003/02/17 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDL.java#16 $ by $Author: rhs $, $DateTime: 2003/02/17 13:30:53 $";

    private AST m_ast = new AST();
    private ErrorReport m_errors = new ErrorReport();
    private SymbolTable m_symbols = new SymbolTable(m_errors);
    private HashMap m_properties = new HashMap();

    public PDL() {}

    public void load(Reader r, String filename) {
        try {
            Parser p = new Parser(r);
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

        public void bind(PreparedStatement ps, int index, Object obj,
                         int type) throws SQLException {
            m_binder.bind(ps, index, obj, type);
        }

        public PropertyMap getProperties(Object obj) {
            return PropertyMap.EMPTY;
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
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBigDecimal
                                (index, new BigDecimal((BigInteger) obj));
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBigDecimal(index, (BigDecimal) obj);
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setString(index, (String) obj);
                        }
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setFloat(index, ((Float) obj).floatValue());
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
                    },
                new Binder() {
                        public void bind(PreparedStatement ps, int index,
                                         Object obj, int type)
                            throws SQLException {
                            ps.setBoolean
                                (index, ((Boolean) obj).booleanValue());
                        }
                    },
                null,
                null,
                null,
                null,
                null,
                null,
                null
            };

            for (int i = 0; i < types.length; i++) {
                ObjectType type = root.getObjectType(types[i]);
                Adapter.addAdapter(classes[i], type,
                                   new SimpleAdapter(type, binders[i]));
            }
        }
    }

    private static interface Binder {
        void bind(PreparedStatement ps, int index, Object obj, int type)
            throws SQLException;
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

    public static final void main(String[] args) throws Exception {
        PDL pdl = new PDL();
        for (int i = 0; i < args.length; i++) {
            pdl.load(new FileReader(args[i]), args[i]);
        }
        pdl.emit(Root.getRoot());
    }

}
