package com.arsdigita.persistence.proto.pdl;

import com.arsdigita.persistence.proto.pdl.nodes.*;
import com.arsdigita.persistence.proto.metadata.Root;
import com.arsdigita.persistence.proto.metadata.Role;
import com.arsdigita.persistence.proto.metadata.Table;
import com.arsdigita.persistence.proto.metadata.ObjectMap;

import java.io.Reader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * PDL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/15 $
 **/

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/PDL.java#4 $ by $Author: rhs $, $DateTime: 2003/01/15 09:35:55 $";

    private AST m_ast = new AST();
    private ErrorReport m_errors = new ErrorReport();
    private SymbolTable m_symbols = new SymbolTable(m_errors);

    public PDL() {}

    public void load(Reader r, String filename) {
        try {
            Parser p = new Parser(r);
            File file = p.file(filename);
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
            m_symbols.addEmitted
                ((com.arsdigita.persistence.proto.metadata.ObjectType) it.next());
        }

        if (root.getObjectType("global.String") == null) {
            loadResource("com/arsdigita/persistence/proto/pdl/global.pdl");
        }

        m_ast.traverse(new Node.Switch() {
                public void onObjectType(ObjectType ot) {
                    m_symbols.define(ot);
                }
            });
        m_ast.traverse(new Node.Switch() {
                public void onType(Type t) {
                    m_symbols.resolve(t);
                }
            });

        m_symbols.sort();
        m_symbols.emit();

        m_ast.traverse(new Node.Switch() {
                private Role define(com.arsdigita.persistence.proto.metadata.ObjectType type, Property prop) {
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
                    return result;
                }

                public void onProperty(Property prop) {
                    define(m_symbols.getEmitted
                           ((ObjectType) prop.getParent()), prop);
                }

                public void onAssociation(Association assn) {
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
                AST.FILES, File.OBJECT_TYPES, ObjectType.PROPERTIES,
                File.ASSOCIATIONS
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
            ObjectType ot = (ObjectType) it.next();
            root.addObjectType(m_symbols.getEmitted(ot));
            root.addObjectMap(new ObjectMap(m_symbols.getEmitted(ot)));
        }

        m_ast.traverse(new Node.Switch() {
                public void onIdentifier(Identifier id) {
                    ObjectType ot = (ObjectType) id.getParent().getParent();
                    ObjectMap om = root.getObjectMap(m_symbols.getEmitted(ot));
                    om.getKeyProperties()
                        .add(m_symbols.getEmitted(ot)
                             .getProperty(id.getName()));
                }
            }, new Node.IncludeFilter(new Node.Field[] {
                AST.FILES, File.OBJECT_TYPES, ObjectType.OBJECT_KEY,
                ObjectKey.PROPERTIES
            }));

        for (Iterator it = root.getObjectTypes().iterator(); it.hasNext(); ) {
            com.arsdigita.persistence.proto.metadata.ObjectType ot =
                (com.arsdigita.persistence.proto.metadata.ObjectType)
                it.next();
            if (ot.getSupertype() != null) {
                ObjectMap om = root.getObjectMap(ot);
                ObjectMap bm = root.getObjectMap(ot.getBasetype());
                om.getKeyProperties().clear();
                om.getKeyProperties().addAll(bm.getKeyProperties());
            }
        }

        emitDDL(root);
    }

    private void emitDDL(Root root) {
        final HashMap tables = new HashMap();
        for (Iterator it = root.getTables().iterator(); it.hasNext(); ) {
            Table table = (Table) it.next();
            tables.put(table.getName(), table);
        }

        m_ast.traverse(new Node.Switch() {
                public void onColumn(Column col) {
                    Table table =
                        (Table) tables.get(col.getTable().getName());
                    if (table == null) {
                        table = new Table(col.getTable().getName());
                        tables.put(table.getName(), table);
                    }
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
