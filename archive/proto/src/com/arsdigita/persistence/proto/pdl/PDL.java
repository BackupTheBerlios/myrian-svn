package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.pdl.nodes.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.io.Reader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * PDL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/01/17 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDL.java#8 $ by $Author: rhs $, $DateTime: 2003/01/17 11:07:02 $";

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

    public void emit(final Root root) {
        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            m_symbols.addEmitted((ObjectType) it.next());
        }

        if (root.getObjectType("global.String") == null) {
            loadResource("com/arsdigita/persistence/proto/pdl/global.pdl");
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
                                 prop.isCollection());
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

        StringBuffer buf = new StringBuffer();
        for (Iterator it = m_errors.getMessages().iterator(); it.hasNext(); ) {
            buf.append(it.next() + "\n");
        }

        if (buf.length() > 0) {
            throw new Error(buf.toString());
        }

        for (Iterator it = m_symbols.getObjectTypes().iterator();
             it.hasNext(); ) {
            ObjectTypeNd ot = (ObjectTypeNd) it.next();
            root.addObjectType(m_symbols.getEmitted(ot));
        }

        emitDDL(root);
        emitMapping(root);
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

                    Column col = table.getColumn(colNd.getName().getName());
                    if (col == null) {
                        col = new Column(colNd.getName().getName());
                        table.addColumn(col);
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
