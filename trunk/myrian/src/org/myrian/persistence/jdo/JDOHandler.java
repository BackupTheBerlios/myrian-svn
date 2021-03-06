package org.myrian.persistence.jdo;

import org.myrian.persistence.PropertyMap;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.pdl.adapters.IdentityAdapter;

import java.lang.reflect.*;
import java.util.*;
import javax.jdo.spi.*;

import org.xml.sax.*;

import org.apache.log4j.Logger;

/**
 * JDOHandler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

class JDOHandler extends ReflectionHandler {

    private final static Logger s_log = Logger.getLogger(JDOHandler.class);

    private final static byte P_MOD_NONE          = (byte) 0;
    private final static byte P_MOD_PERSISTENT    = (byte) 1;
    private final static byte P_MOD_TRANSACTIONAL = (byte) P_MOD_PERSISTENT << 1;
    private final static byte P_MOD_DEFAULT       = (byte) P_MOD_PERSISTENT << 2;

    private final static String INDENT = "    ";

    private ClassLoader m_loader;
    private String m_resource;
    private Locator m_locator = null;
    private String m_package = null;

    private Class m_class = null;
    private Class m_super = null;
    private List m_keys = new ArrayList();
    private byte m_persistenceModifier = P_MOD_DEFAULT;

    private boolean m_generate = true;
    private int m_line = -1;

    private Field m_field = null;
    private Class m_type = null;
    private boolean m_embedded = false;
    private boolean m_required = false;
    private boolean m_collection = false;
    private Class m_key = null;
    private Class m_value = null;
    private String m_jdbcColumn = null;
    private Integer m_jdbcType = null;
    private Integer m_jdbcSize = null;

    private Map m_fields = new LinkedHashMap();

    private StringBuffer m_emitted = new StringBuffer();

    JDOHandler(ClassLoader loader, String resource) {
        m_loader = loader;
        m_resource = resource;
    }

    public void setDocumentLocator(Locator locator) {
        m_locator = locator;
    }

    private Error fatal(String message, Throwable cause) {
        Error e = fatal(message);
        e.initCause(cause);
        return e;
    }

    private Error fatal(Throwable t) {
        return fatal(t.getMessage(), t);
    }

    private Error fatal(String message) {
        return new Error(m_resource + ":" + m_locator.getLineNumber() +
                         "," + m_locator.getColumnNumber() + ": " + message);
    }

    public void start(String name, Attributes attributes) {
        throw fatal("unknown element '" + name + "'");
    }

    private List getCandidates(String name) {
        List result = new ArrayList();
        result.add(name);
        if (name.indexOf('.') < 0) {
            if (m_package != null) {
                result.add(m_package + "." + name);
            }
            result.add("java.lang." + name);
            result.add("java.math." + name);
            result.add("java.util." + name);
        }
        return result;
    }

    private Class forName(String name) {
        List candidates = getCandidates(name);
        for (int i = 0; i < candidates.size(); i++) {
            String candidate = (String) candidates.get(i);
            try {
                // We don't want to initialize the class we're loading
                // here because we will be called from a static block
                // of a class, and forcing initialization here can
                // create a cyclic initialization dependency which in
                // turn can break static code.
                return Class.forName(candidate, false, m_loader);
            } catch (ClassNotFoundException e) {
                // ignore
            }
        }
        throw fatal("unable to locate any of: " + candidates);
    }

    private JDOHandler emit(String pdl) {
        m_emitted.append(pdl);
        return this;
    }

    public String getPDL() {
        return m_emitted.toString();
    }

    public void startJdo(Attributes attributes) {}
    public void endJdo() {}

    private static final String VENDOR = "myrian";

    public void startExtension(Attributes attributes) {
        String vendor = attributes.getValue("vendor-name");
        if (VENDOR.equals(vendor)) {
            String key = attributes.getValue("key");
            String value = attributes.getValue("value");
            if (!invoke("handle" + studly(key), new Object[] { value })) {
                throw fatal("unknown extension: " + key);
            }
        }
    }

    public void startPackage(Attributes attributes) {
        m_package = attributes.getValue("name");
        emit("model " + m_package + "; ");
    }

    public void endPackage() {
        m_package = null;
    }

    public void startClass(Attributes attributes) {
        String name = attributes.getValue("name");
        String identity = attributes.getValue("identity-type");
        String oidClass = attributes.getValue("objectid-class");
        String extent = attributes.getValue("requires-extent");
        String pcSuper = attributes.getValue("persistence-capable-superclass");

        String qname = m_package == null ? name : m_package + "." + name;
        m_class = forName(qname);
        if (pcSuper != null) {
            m_super = forName(pcSuper);
        }
        m_line = m_locator.getLineNumber();
    }

    private boolean isTrue(String value, boolean dephault) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        } else if (value == null) {
            return dephault;
        } else {
            throw fatal("bad boolean value");
        }
    }

    private static byte pModFlag(String flag) {
        if (flag == null || "".equals(flag) ) {
            return P_MOD_DEFAULT;
        }
        final String lcase = flag.toLowerCase();
        if ("none".equals(lcase)) {
            return P_MOD_NONE;
        } else if ("transactional".equals(lcase)) {
            return P_MOD_TRANSACTIONAL;
        } else if ("persistent".equals(lcase)) {
            return P_MOD_PERSISTENT;
        } else {
            throw new IllegalStateException("unknown modifier: " + flag);
        }
    }

    private static boolean isPModPersistent(byte flag) {
        return (P_MOD_PERSISTENT & flag) > 0;
    }

    private static boolean isPModDefault(byte flag) {
        return (P_MOD_DEFAULT & flag) > 0;
    }

    public void handleMetadata(String value) {
        if ("XML".equals(value)) {
            m_generate = true;
        } else if ("PDL".equals(value)) {
            m_generate = false;
        } else {
            throw fatal("unknown metadata type: " + value);
        }
    }

    public void startField(Attributes attributes) {
        String name = attributes.getValue("name");
        String primary = attributes.getValue("primary-key");
        String nullValue = attributes.getValue("null-value");
        String fetch = attributes.getValue("default-fetch-group");
        String embedded = attributes.getValue("embedded");
        m_embedded = isTrue(embedded, false);
        m_persistenceModifier =
            pModFlag(attributes.getValue("persistence-modifier"));
        try {
            m_field = m_class.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            throw fatal(e);
        }

        m_type = m_field.getType();
    }

    public void handlePrimaryKey(String value) {
        if (isTrue(value, false)) {
            m_keys.add(m_field);
        }
    }

    public void handleJdbcColumn(String value) {
        m_jdbcColumn = value;
    }

    public void handleJdbcSize(String value) {
        try {
            m_jdbcSize = Integer.valueOf(value);
        } catch (Throwable t) {
            throw fatal("bad jdbc size: " + value, t);
        }
    }

    public void handleJdbcType(String value) {
        Class klass = java.sql.Types.class;
        try {
            Field f = klass.getField(value);
            m_jdbcType = new Integer(f.getInt(null));
        } catch (NoSuchFieldException e) {
            throw fatal("bad jdbc type: " + value, e);
        } catch (IllegalAccessException e) {
            throw fatal("bad jdbc type: " + value, e);
        }
    }

    private boolean isList(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

    public void startCollection(Attributes attributes) {
        String type = attributes.getValue("element-type");
        String embedded = attributes.getValue("embedded-element");
        m_embedded = isTrue(embedded, false);
        m_collection = true;
        if (type != null) {
            m_type = forName(type);
        } else {
            m_type = forName("java.lang.Object");
        }
        if (isList(m_field)) {
            m_key = forName("Integer");
            m_value = m_type;
            m_embedded = true;
        }
    }

    public void startMap(Attributes attributes) {
        String keyType = attributes.getValue("key-type");
        if (keyType != null) {
            m_key = forName(keyType);
        } else {
            m_type = forName("java.lang.Object");
        }
        String valueType = attributes.getValue("value-type");
        if (valueType != null) {
            m_value = forName(valueType);
        } else {
            m_type = forName("java.lang.Object");
        }
        String embeddedKey = attributes.getValue("embedded-key");
        String embeddedValue = attributes.getValue("embedded-value");
        m_collection = true;
        m_embedded = true;
    }

    private String classname(String qname) {
        int idx = qname.lastIndexOf('.');
        if (idx < 0) {
            return qname;
        } else {
            return qname.substring(idx + 1);
        }
    }

    String table(Class klass) {
        return classname(klass.getName()).toLowerCase() + "s";
    }

    String table(Class klass, String field) {
        return table(klass) + "_" + field.toLowerCase();
    }

    String table(Class klass, Field field) {
        return table(klass, field.getName());
    }

    String column(String table, String column) {
        return table.toLowerCase() + "." + column.toLowerCase();
    }

    String column(Class klass) {
        return column(table(klass), "_jdo_id");
    }

    String column(Class klass, String field) {
        return column(table(klass), field);
    }

    String value(String column) {
        return "value(" + column + ")";
    }

    String reference(String column) {
        return "reference(" + column + ")";
    }

    String inverse(String column) {
        return "inverse(" + column + ")";
    }

    String mapping(String from, String to) {
        return "mapping(" + from + ", " + to + ")";
    }

    String mapping(String from, String field, String to, boolean collection,
                   boolean embedded) {
        if (collection && embedded) {
            return inverse(column(from + "_" + field, "_jdo_id"));
        } else if (collection && !embedded) {
            String map = from + "_" + field;
            return mapping(column(map, "_jdo_id"), column(map, field));
        } else if (!collection && embedded) {
            return value(column(from, field));
        } else if (!collection && !embedded) {
            return reference(column(from, field));
        } else {
            throw new IllegalStateException();
        }
    }

    private boolean isPC(Class klass) {
        return PersistenceCapable.class.isAssignableFrom(klass);
    }

    public void endField() {
        final String type;
        if (m_key == null) {
            type = pdlType(m_type);
        } else {
            type = MapEntry.class.getName() +
                "<" + pdlType(m_key) + ", " + pdlType(m_value) + ">";
        }

        StringBuffer pdl = new StringBuffer();
        pdl.append(INDENT);
        if (m_key != null) { pdl.append("component "); }
        pdl.append(type);
        if (m_required || m_collection) {
            pdl.append("[" + (m_required ? "1" : "0") + ".." +
                       (m_collection ? "n" : "1") + "]");
        }
        String field = m_field.getName();

        if (!isPModDefault(m_persistenceModifier) &&
            !isPModPersistent(m_persistenceModifier)) {

            m_fields.put(m_field, INDENT + "// ignored: " + type + " " + field);
            return;
        }

        pdl.append(" " + field);
        if (isList(m_field)) { pdl.append("$elements"); }
        if (m_key != null) { pdl.append("$entries"); }
        if (m_embedded && !m_collection && isPC(m_type)) {
            pdl.append(" {\n");
            // XXX: superclass fields?
            Field[] fields = m_type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                if (!isPersistent(fields[i])) { continue; }
                pdl.append("        ");
                pdl.append(fields[i].getName());
                pdl.append(" = ");
                pdl.append(mapping(table(m_class),
                                   field + "_" + fields[i].getName(),
                                   table(fields[i].getType()),
                                   false, !isPC(fields[i].getType())));
                pdl.append(";\n");
            }
            pdl.append("\n    }");
        } else {
            pdl.append(" = ");
            pdl.append(mapping(table(m_class), field, table(m_type),
                               m_collection, m_embedded || !isPC(m_type)));
            if (m_key != null) {
                pdl.append(" {\n");
                pdl.append("        key = ");
                pdl.append(mapping(table(m_class) + "_" + field, "key",
                                   table(m_key), false, !isPC(m_key)));
                pdl.append(";\n");
                pdl.append("        value = ");
                pdl.append(mapping(table(m_class) + "_" + field, "value",
                                   table(m_value), false, !isPC(m_key)));
                pdl.append(";\n");
                pdl.append("        object key (key);\n");
                pdl.append("    }");
            }
        }
        pdl.append(";");
        m_fields.put(m_field, pdl.toString());
        m_field = null;
        m_type = null;
        m_key = null;
        m_value = null;
        m_required = false;
        m_collection = false;
        m_embedded = false;
        m_jdbcColumn = null;
        m_jdbcType = null;
        m_jdbcSize = null;
    }

    public void endClass() {
        if (m_generate) {
            Field[] fields = m_class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (m_fields.containsKey(field)) {
                    continue;
                }
                if (isPersistent(field)) {
                    StringBuffer pdl = new StringBuffer();
                    pdl.append(INDENT + pdlType(field.getType()) + " " +
                               field.getName());
                    pdl.append(" = ");
                    pdl.append(mapping(table(m_class), field.getName(),
                                       table(field.getType()), false,
                                       !isPC(field.getType())));
                    pdl.append(";");
                    m_fields.put(field, pdl.toString());
                }
            }

            emit("\n");

            String name = classname(m_class.getName());
            emit("object type " + name);
            if (m_super != null) {
                emit(" extends " + m_super.getName());
            }
            emit(" class ").emit(m_class.getName());
            emit(" adapter ").emit(JDOAdapter.class.getName());
            emit(" {\n");
            if (m_keys.isEmpty() && m_super == null) {
                emit("    Integer _jdo_id = " + value(column(m_class)) +
                     ";\n");
            }

            for (Iterator it = m_fields.keySet().iterator(); it.hasNext(); ) {
                Object field = it.next();
                String pdl = (String) m_fields.get(field);
                emit(pdl + "\n");
            }

            if (!m_keys.isEmpty()) {
                emit("\n    object key (");
                for (int i = 0; i < m_keys.size(); i++) {
                    Field key = (Field) m_keys.get(i);
                    emit(key.getName());
                    if (i < (m_keys.size() - 1)) {
                        emit(", ");
                    }
                }
                emit(");\n");
            } else if (m_super == null) {
                emit("\n    object key (_jdo_id);\n");
            } else {
                emit("\n    reference key (" + column(m_class) + ");\n");
            }
            emit("\n}");

            if (m_keys.isEmpty() && m_super == null) {
                emit("\nquery " + name + "$Gen class " +
                     PropertyMap.class.getName() + " adapter " +
                     IdentityAdapter.class.getName() + " {\n");
                emit("    Integer _jdo_id;\n");
                emit("    do {\n");
                emit("        select nextval('jdotest_seq') as id\n");
                emit("    } map {\n");
                emit("        _jdo_id = id;\n");
                emit("    }\n");
                emit("}\n");
            }
        }

        m_fields.clear();
        m_class = null;
        m_super = null;
        m_generate = true;
        m_keys.clear();
        m_line = -1;
    }

    private static final Map J2P_SUBS = new HashMap();
    static {
        // primitives
        J2P_SUBS.put(boolean.class, "Boolean");
        J2P_SUBS.put(byte.class, "Byte");
        J2P_SUBS.put(short.class, "Short");
        J2P_SUBS.put(int.class, "Integer");
        J2P_SUBS.put(long.class, "Long");
        J2P_SUBS.put(char.class, "Character");
        J2P_SUBS.put(float.class, "Float");
        J2P_SUBS.put(double.class, "Double");

        // wrappers
        J2P_SUBS.put(Boolean.class, "Boolean");
        J2P_SUBS.put(Byte.class, "Byte");
        J2P_SUBS.put(Short.class, "Short");
        J2P_SUBS.put(Integer.class, "Integer");
        J2P_SUBS.put(Long.class, "Long");
        J2P_SUBS.put(Character.class, "Character");
        J2P_SUBS.put(Float.class, "Float");
        J2P_SUBS.put(Double.class, "Double");

        // arrays
        J2P_SUBS.put(byte[].class, "byte[]");

        // java.lang
        J2P_SUBS.put(String.class, "String");
        J2P_SUBS.put(Number.class, "Number");

        // java.math
        J2P_SUBS.put(java.math.BigDecimal.class, "BigDecimal");
        J2P_SUBS.put(java.math.BigInteger.class, "BigInteger");

        // java.util
        J2P_SUBS.put(java.util.Date.class, "Date");
        J2P_SUBS.put(java.util.Locale.class, "Locale");
    }

    private String pdlType(Class klass) {
        String sub = (String) J2P_SUBS.get(klass);
        if (sub == null) {
            return klass.getName();
        } else {
            return sub;
        }
    }

    private static final Set IGNORED = new HashSet();
    static {
        IGNORED.add("jdoStateManager");
        IGNORED.add("jdoFlags");
        IGNORED.add("jdoInheritedFieldCount");
        IGNORED.add("jdoFieldNames");
        IGNORED.add("jdoFieldTypes");
        IGNORED.add("jdoFieldFlags");
        IGNORED.add("jdoPersistenceCapableSuperclass");
    }

    private static final Set PERSISTENT_TYPES = new HashSet();
    static {
        // primitives
        PERSISTENT_TYPES.add("boolean");
        PERSISTENT_TYPES.add("byte");
        PERSISTENT_TYPES.add("short");
        PERSISTENT_TYPES.add("int");
        PERSISTENT_TYPES.add("long");
        PERSISTENT_TYPES.add("char");
        PERSISTENT_TYPES.add("float");
        PERSISTENT_TYPES.add("double");

        // wrappers
        PERSISTENT_TYPES.add("java.lang.Boolean");
        PERSISTENT_TYPES.add("java.lang.Byte");
        PERSISTENT_TYPES.add("java.lang.Short");
        PERSISTENT_TYPES.add("java.lang.Integer");
        PERSISTENT_TYPES.add("java.lang.Long");
        PERSISTENT_TYPES.add("java.lang.Character");
        PERSISTENT_TYPES.add("java.lang.Float");
        PERSISTENT_TYPES.add("java.lang.Double");

        // java.lang
        PERSISTENT_TYPES.add("java.lang.String");
        PERSISTENT_TYPES.add("java.lang.Number");

        // java.math
        PERSISTENT_TYPES.add("java.math.BigDecimal");
        PERSISTENT_TYPES.add("java.math.BigInteger");

        // java.util
        PERSISTENT_TYPES.add("java.util.Date");
        PERSISTENT_TYPES.add("java.util.Locale");
        PERSISTENT_TYPES.add("java.util.ArrayList");
        PERSISTENT_TYPES.add("java.util.HashMap");
        PERSISTENT_TYPES.add("java.util.HashSet");
        PERSISTENT_TYPES.add("java.util.Hashtable");
        PERSISTENT_TYPES.add("java.util.LinkedList");
        PERSISTENT_TYPES.add("java.util.TreeMap");
        PERSISTENT_TYPES.add("java.util.TreeSet");
        PERSISTENT_TYPES.add("java.util.Vector");
        PERSISTENT_TYPES.add("java.util.Collection");
        PERSISTENT_TYPES.add("java.util.Set");
        PERSISTENT_TYPES.add("java.util.List");
        PERSISTENT_TYPES.add("java.util.Map");
    }

    private boolean isPersistent(Field field) {
        // explicitly ignore fields added by the reference enhancer
        if (IGNORED.contains(field.getName())) {
            return false;
        }

        int mods = field.getModifiers();
        if (((Modifier.STATIC |
              Modifier.TRANSIENT |
              Modifier.FINAL) & mods) != 0) {
            return false;
        }

        Class type = field.getType();

        if (PersistenceCapable.class.isAssignableFrom(type)) {
            return true;
        }

        if (PERSISTENT_TYPES.contains(type.getName())) {
            return true;
        }

        // XXX: arrays

        return false;
    }

}
