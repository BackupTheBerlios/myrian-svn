package org.myrian.persistence.metadata;

import java.util.*;

/**
 * TypeReference
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public abstract class TypeReference {


    private static final ObjectType UNRESOLVED =
        new ObjectType(null, "#unresolved", null);

    public static TypeReference get(final ObjectType type) {
        return new TypeReference() {
            public ObjectType get() {
                return type;
            }
            TypeReference substitute(Map env) { return this; }
        };
    }

    public static TypeReference get(final Root root, final String qname) {
        return new TypeReference() {
            public ObjectType get() {
                return root.getObjectType(qname);
            }
            TypeReference substitute(Map env) { return this; }
        };
    }

    public static TypeReference get(final TypeReference template,
                                    final List arguments) {
        return new TypeReference() {
            private ObjectType m_type = null;
            public ObjectType get() {
                if (m_type == null) {
                    m_type = instantiate();
                }
                return m_type;
            }
            private ObjectType instantiate() {
                ObjectType tmpl = template.get();
                if (tmpl == UNRESOLVED) {
                    return UNRESOLVED;
                }
                List args = new ArrayList();
                for (int i = 0; i < arguments.size(); i++) {
                    ObjectType arg = ((TypeReference) arguments.get(i)).get();
                    if (arg == UNRESOLVED) {
                        return UNRESOLVED;
                    }
                    args.add(arg);
                }
                StringBuffer buf = new StringBuffer();
                buf.append(tmpl.getName());
                buf.append("<");
                for (int i = 0; i < args.size(); i++) {
                    buf.append(((ObjectType) args.get(i)).getQualifiedName());
                    buf.append(", ");
                }
                buf.setLength(buf.length() - 2);
                buf.append(">");
                String name = buf.toString();
                Model model = tmpl.getModel();
                String qname = model == null ?
                    name : model.getQualifiedName() + "." + name;
                Root root = tmpl.getRoot();
                synchronized (root) {
                    if (root.hasObjectType(qname)) {
                        return root.getObjectType(qname);
                    }
                    Map env = new HashMap();
                    List params = tmpl.getParameters();
                    int idx = 0;
                    for (Iterator it = params.iterator(); it.hasNext(); ) {
                        env.put(it.next(), arguments.get(idx++));
                    }
                    ObjectType inst = new ObjectType
                        (model, name, null,
                         sub(tmpl.getSupertypeReference(), env));
                    Collection props = tmpl.getDeclaredProperties();
                    for (Iterator it = props.iterator(); it.hasNext(); ) {
                        Role role = (Role) it.next();
                        Role irole = new Role
                            (role.getName(), sub(role.getTypeReference(), env),
                             role.isComponent(), role.isCollection(),
                             role.isNullable());
                        inst.addProperty(irole);
                    }
                    root.addObjectType(inst);
                    return inst;
                }
            }
            TypeReference substitute(Map env) {
                return get(sub(template, env), subl(arguments, env));
            }
        };
    }

    public static TypeReference get(final String parameter) {
        return new TypeReference() {
            public ObjectType get() {
                return UNRESOLVED;
            }
            TypeReference substitute(Map env) {
                return (TypeReference) env.get(parameter);
            }
        };
    }

    private static TypeReference sub(TypeReference tref, Map env) {
        if (tref == null) { return null; }
        return tref.substitute(env);
    }

    private static List subl(List trefs, Map env) {
        List result = new ArrayList(trefs.size());
        for (int i = 0; i < trefs.size(); i++) {
            TypeReference tref = (TypeReference) trefs.get(i);
            result.add(sub(tref, env));
        }
        return result;
    }

    public abstract ObjectType get();

    abstract TypeReference substitute(Map environment);

}
