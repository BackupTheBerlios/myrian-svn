/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.math.*;

import org.apache.log4j.Logger;
import com.arsdigita.db.DbHelper;

/**
 * The MetadataRoot is a singleton class that serves as an entry point for the
 * metadata system.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2002/08/14 $
 **/

public class MetadataRoot extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/MetadataRoot.java#8 $ by $Author: dan $, $DateTime: 2002/08/14 05:45:56 $";

    private static final Logger s_cat = Logger.getLogger(MetadataRoot.class.getName());

    /**
     * The MetadataRoot instance for this JVM.
     **/
    private static MetadataRoot s_root;
    // the following is a list of constants that can be used to specify the
    // type
    public static final SimpleType BIGINTEGER = init(
    new SimpleType("BigInteger", java.math.BigInteger.class, Types.NUMERIC) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                BigInteger bigInt = (BigInteger) value;
                ps.setBigDecimal(index, new BigDecimal(bigInt));
                return 1;
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                BigDecimal bd = rs.getBigDecimal(column);
                if (bd == null) {
                    return null;
                } else {
                    BigInteger val = bd.toBigInteger();
                    if( val.bitLength() < 32 ) {
                        val = new SmallIntBigInteger( val.toByteArray() );
                    }
                    return val;
                }
            }
        });

    public static final SimpleType BIGDECIMAL = init(
    new SimpleType("BigDecimal", java.math.BigDecimal.class, Types.NUMERIC) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setBigDecimal(index, (BigDecimal) value);
                return 1;
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                BigDecimal val = rs.getBigDecimal(column);
                // Value is really an Integer
                if( null != val && val.scale() == 0 && val.toBigInteger().bitLength() < 32) {
                    val = new SmallIntBigDecimal(val.toBigInteger());
                }

                return val;
            }
        });

    public static final SimpleType BOOLEAN = init(
    new SimpleType("Boolean", java.lang.Boolean.class, Types.BIT) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                // Because postgres has a boolean type and we are currently
                // using char(1) and the jdbc driver uses "false" and "true"
                // we have to do this converstion.  Long term, we want
                // to remove this
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    boolean booleanValue = ((Boolean) value).booleanValue();
                    if (booleanValue) {
                        ps.setString(index, "1");
                    } else {
                        ps.setString(index, "0");
                    }
                } else {
                    ps.setBoolean(index, ((Boolean) value).booleanValue());
                }
                return 1;
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
        });

    public static final SimpleType BYTE = init(
    new SimpleType("Byte", java.lang.Byte.class, Types.TINYINT) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setByte(index, ((Byte) value).byteValue());
                return 1;
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
        });

    public static final SimpleType CHARACTER = init(
    new SimpleType("Character", java.lang.Character.class, Types.CHAR) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setString(index, ((Character) value).toString());
                return 1;
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
        });

    public static final SimpleType DATE = init(
    new SimpleType("Date", java.util.Date.class, Types.TIMESTAMP) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                Timestamp tstamp =
                    new Timestamp(((java.util.Date) value).getTime());
                ps.setTimestamp(index, tstamp);
                return 1;
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
        });

    public static final SimpleType DOUBLE = init(
    new SimpleType("Double", java.lang.Double.class, Types.DOUBLE) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setDouble(index, ((Double) value).doubleValue());
                return 1;
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
        });

    public static final SimpleType FLOAT = init(
    new SimpleType("Float", java.lang.Float.class, Types.REAL) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setFloat(index, ((Float) value).floatValue());
                return 1;
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
        });

    public static final SimpleType INTEGER = init(
    new SimpleType("Integer", java.lang.Integer.class, Types.INTEGER) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setInt(index, ((Integer) value).intValue());
                return 1;
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
        });

    public static final SimpleType LONG = init(
    new SimpleType("Long", java.lang.Long.class, Types.BIGINT) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setLong(index, ((Long) value).longValue());
                return 1;
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
        });

    public static final SimpleType SHORT = init(
    new SimpleType("Short", java.lang.Short.class, Types.SMALLINT) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setShort(index, ((Short) value).shortValue());
                return 1;
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
        });

    public static final SimpleType STRING = init(
    new SimpleType("String", java.lang.String.class, Types.VARCHAR) {
            public boolean needsRefresh(Object value, int jdbcType) {
                return (value != null && jdbcType == Types.CLOB);
            }

            public void doRefresh(ResultSet rs, String column, Object value)
                throws SQLException {
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    // do nothing
                } else {
                    oracle.sql.CLOB clob =
                        (oracle.sql.CLOB) rs.getClob(column);
                    Writer out = clob.getCharacterOutputStream();
                    try {
                        out.write(((String) value).toCharArray());
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        // This used to be a persistence exception, but using
                        // persistence exception here breaks ant verify-pdl
                        // because the classpath isn't set up to include
                        // com.arsdigita.util.*
                        throw new Error("Unable to write LOB: " + e);
                    }
                }
            }

            public String getLiteral(Object value, int jdbcType) {
                switch (jdbcType) {
                case Types.CLOB:
                    if (value == null) {
                        return super.getLiteral(value, jdbcType);
                    } else {
                        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                            return " ? ";
                        } else {
                            return " empty_clob() ";
                        }
                    }

                default:
                    return super.getLiteral(value, jdbcType);
                }
            }

            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                switch (jdbcType) {
                case Types.CLOB:
                    //CLOB.bind(ps, index, value, jdbcType);
                    if (DbHelper.getDatabase() != DbHelper.DB_POSTGRES) {
                        return 0;
                    }
                default:
                    ps.setString(index, (String) value);
                    break;
                }

                return 1;
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                ResultSetMetaData md = rs.getMetaData();
                if (md.getColumnType(rs.findColumn(column)) == Types.CLOB &&
                    DbHelper.getDatabase() != DbHelper.DB_POSTGRES) {
                    return CLOB.fetch(rs, column);
                } else {
                    return rs.getString(column);
                }
            }
        });

    public static final SimpleType BLOB = init(
    new SimpleType("Blob", java.sql.Blob.class, Types.BLOB) {
            public boolean needsRefresh(Object value, int jdbcType) {
                return (value != null && jdbcType == Types.BLOB);
            }

            public void doRefresh(ResultSet rs, String column, Object value)
                throws SQLException {
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    // do nothing
                } else {
                    oracle.sql.BLOB blob =
                        (oracle.sql.BLOB) rs.getBlob(column);
                    OutputStream out = blob.getBinaryOutputStream();
                    try {
                        out.write((byte[]) value);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        // This used to be a persistence exception, but using
                        // persistence exception here breaks ant verify-pdl
                        // because the classpath isn't set up to include
                        // com.arsdigita.util.*
                        throw new Error("Unable to write LOB: " + e);
                    }
                }
            }

            public String getLiteral(Object value, int jdbcType) {
                switch (jdbcType) {
                case Types.BLOB:
                    if (value == null) {
                        return super.getLiteral(value, jdbcType);
                    } else {
                        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                            return " ? ";
                        } else {
                            return " empty_blob() ";
                        }
                    }

                default:
                    return super.getLiteral(value, jdbcType);
                }
            }

            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    byte[] bytes = (byte[]) value;
                // This is supported by the oracle OCI driver and the PG driver
                // http://www.oradoc.com/ora816/java.816/a81354/oralob2.htm#1058119
                    ps.setBinaryStream(index, new ByteArrayInputStream(bytes),
                                       bytes.length);
                    return 1;
                } else {
                    return 0;
                }
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    return rs.getBytes(column);
                } else {
                    Blob blob = rs.getBlob(column);
                    if (blob == null) {
                        return null;
                    } else {
                        return blob.getBytes(1L, (int)blob.length());
                    }
                }
            }
        });

    public static final SimpleType CLOB = init(
    new SimpleType("Clob", java.sql.Clob.class, Types.CLOB) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                String str = (String) value;
                 // This is only supported by the OCI driver
                 // http://www.oradoc.com/ora816/java.816/a81354/oralob2.htm#1058119
                 // From Oracle release notes:
                 //BUG-1018797
                 //    Extra characters may be appended to the end of a CLOB value
                 //    mistakenly under the following conditions:
                 //    - setCharacterStream() is used to insert a CLOB value, and
                 //    - The Oracle server uses multi-byte character set.
                 //    (See 1 for limitation of stream input for LOB type.)
                ps.setCharacterStream(
                    index,
                    new StringReader(str),
                    str.length()
                    );

                return 1;
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                Clob clob = rs.getClob(column);
                if (clob == null) {
                    return null;
                } else {
                    return clob.getSubString(1L, (int)clob.length());
                }
            }
        });

    // This is for backword compatibility with data queries.
    public static final SimpleType OBJECT = init(
    new SimpleType("<Object>", java.lang.Object.class, Types.VARCHAR) {
            public int bindValue(PreparedStatement ps, int index, Object value,
                             int jdbcType) throws SQLException {
                ps.setObject(index, value);
                return 1;
            }

            public Object fetch(ResultSet rs, String column)
                throws SQLException {
                ResultSetMetaData md = rs.getMetaData();
                if (md.getColumnType(rs.findColumn(column)) == Types.CLOB) {
                    return CLOB.fetch(rs, column);
                } else {
                    return rs.getObject(column);
                }
            }
        });

    private static final SimpleType init(SimpleType type) {
        type.setFilename("MetadataRoot.java");
        type.setLineInfo(32, 14);
        return type;
    }

    static {
        s_root = newInstance();
    }

    /**
     *  Initializes the static MetadataRoot, and adds all the primitive types.
     *  Function is package private for use by JUnit tests. It should NOT be used
     *  for any other purpose!
     */
    static MetadataRoot newInstance()  {
        MetadataRoot result = new MetadataRoot();
        // Set up the primitive types.
        result.addPrimitiveType(BIGINTEGER);
        result.addPrimitiveType(BIGDECIMAL);
        result.addPrimitiveType(BOOLEAN);
        result.addPrimitiveType(BYTE);
        result.addPrimitiveType(CHARACTER);
        result.addPrimitiveType(DATE);
        result.addPrimitiveType(DOUBLE);
        result.addPrimitiveType(FLOAT);
        result.addPrimitiveType(INTEGER);
        result.addPrimitiveType(LONG);
        result.addPrimitiveType(SHORT);
        result.addPrimitiveType(STRING);
        result.addPrimitiveType(BLOB);
        result.addPrimitiveType(CLOB);
        result.setFilename("MetadataRoot.java");
        result.setLineInfo(32, 14);
        return result;
    }



    /**
     * Returns the MetadataRoot instance for this JVM.
     *
     * @return The MetadataRoot instance for this JVM.
     **/
    public static final MetadataRoot getMetadataRoot() {
        return s_root;
    }


    /**
     * The models contained by this MetadataRoot.
     **/
    private Map m_models = new HashMap();
    private Map m_modelsNoMod = Collections.unmodifiableMap(m_models);

    /**
     * The primitive types.
     **/
    private Map m_primitives = new HashMap();
    private Map m_tables = new HashMap();


    /**
     * Package private to enforce the singletonness of this class.
     **/

    MetadataRoot() {}

    public void addTable(Table table) {
        if (hasTable(table.getName())) {
            throw new IllegalArgumentException(
                "This MetadataRoot already has a table named: " +
                table.getName()
                );
        }

        m_tables.put(table.getName(), table);
    }

    public Table getTable(String name) {
        return (Table) m_tables.get(name);
    }

    public boolean hasTable(String name) {
        return m_tables.containsKey(name);
    }

    public Collection getTables() {
        return m_tables.values();
    }

    /**
     * Adds the given Model to this MetadataRoot.
     *
     * @param model The Model to add.
     *
     * @pre hasModel(model.getName()) == false
     * @post hasModel(model.getName())
     *
     * @exception IllegalArgumentException If the model is already contained.
     **/

    public void addModel(Model model) {
        if (hasModel(model.getName())) {
            throw new IllegalArgumentException(
                "This MetadataRoot already has a model named: " +
                model.getName()
                );
        }

        m_models.put(model.getName(), model);
    }


    /**
     * Returns the Model with the specified name, or null if no such model
     * exists.
     *
     * @param name The name of the model to get.
     *
     * @return The specified Model, or null.
     **/

    public Model getModel(String name) {
        Object result = m_models.get(name);
        if (result == null)
            result = caseInsensativeGet(m_models, name);
        return (Model) result;
    }


    /**
     * Returns true if a model with the given name exists as part of this
     * MetadataRoot.
     *
     * @return True if a model with the given name exists as part of this
     *         MetadataRoot.
     **/

    public boolean hasModel(String name) {
        return m_models.containsKey(name);
    }


    /**
     * Returns an Iterator of all the Models contained by this MetadataRoot.
     *
     * @return An Iterator containing instances of Model.
     *
     * @see Model
     **/

    public Iterator getModels() {
        return m_modelsNoMod.values().iterator();
    }

    private String parseModel(String name) {
        int dot = name.lastIndexOf('.');
        int length = name.length();
        if (dot == -1 || dot == length - 1) {
            return null;
        } else {
            return name.substring(0, dot);
        }
    }

    private String parseName(String name) {
        int dot = name.lastIndexOf('.');
        int length = name.length();
        if (dot == -1 || dot == length - 1) {
            return name;
        } else {
            return name.substring(dot + 1, length);
        }
    }

    /**
     * Returns an ObjectType given a fully qualified type name or null if no
     * such type exists. The fully qualified name consists of the model name,
     * followed by a '.' followed by the type name.
     *
     * @param name The fully qualified name of the ObjectType.
     *
     * @return The ObjectType or null.
     **/

    public ObjectType getObjectType(String name) {
        String model = parseModel(name);

        if (model == null) {
            return null;
        }

        Model m = getModel(model);
        if (m == null) {
            return null;
        }

        return m.getObjectType(parseName(name));
    }

    /**
     * Returns a collection of the object types in this metadata root
     *
     * @return a collection of the object types in this metadata root
     */
    public Collection getObjectTypes() {
        Iterator it = m_models.values().iterator();
        Collection retval = new ArrayList();

        while (it.hasNext()) {
            Model m = (Model)it.next();

            retval.addAll(m.getObjectTypes());
        }

        return retval;
    }

    public Set getAssociations() {
        Iterator it = m_models.values().iterator();
        Set retval = new HashSet();

        while (it.hasNext()) {
            Model m = (Model)it.next();

            retval.addAll(m.getAssociations());
        }

        return retval;
    }

    /**
     * Gets the QueryType given the fully qualified type name of the query.
     * The fully qualified type name of the query is the model name followed
     * by a '.' followed by the name of the query.
     *
     * @param name The fully qualified query name.
     *
     * @return QueryType The QueryType with the given name.
     **/

    public QueryType getQueryType(String name) {
        String model = parseModel(name);

        if (model == null) {
            return null;
        }

        Model m = getModel(model);
        if (m == null) {
            return null;
        }

        return m.getQueryType(parseName(name));
    }


    /**
     * Gets the DataOperationType given the fully qualified name of the
     * operation. The fully qualified name of the operation is the model name
     * followed by a '.' followed by the name of the operation.
     *
     * @param name The fully qualified operation name.
     *
     * @return DataOperationType The DataOperationType with the given name.
     **/

    public DataOperationType getDataOperationType(String name) {
        String model = parseModel(name);

        if (model == null) {
            return null;
        }

        Model m = getModel(model);
        if (m == null) {
            return null;
        }

        return m.getDataOperationType(parseName(name));
    }


    /**
     * Returns one of the primitive or predefined types.
     *
     * @param name The name of the primitive type.
     *
     * @return The type.
     **/

    public SimpleType getPrimitiveType(String name) {
        Object result = m_primitives.get(name);
        if (result == null) {
            result = caseInsensativeGet(m_primitives, name);
        }
        return (SimpleType) result;
    }



    /**
     * Adds a new primitive datatype to this metadata root.
     *
     * @param type The primitive type to add.
     **/

    private void addPrimitiveType(SimpleType type) {
        m_primitives.put(type.getName(), type);
    }

    public void outputPDL(PrintStream out) {
        for (Iterator it = getModels(); it.hasNext(); ) {
            Model m = (Model) it.next();
            out.println();
            m.outputPDL(out);
            out.println();
        }
    }

    private Set m_generatedTypes = new HashSet();
    private Set m_generatedAssns = new HashSet();

    public void generateDDL() {
        Collection types = getObjectTypes();
        types.removeAll(m_generatedTypes);
        Set assns = getAssociations();
        assns.removeAll(m_generatedAssns);

        for (Iterator it = types.iterator(); it.hasNext(); ) {
            ObjectType ot = (ObjectType) it.next();
            ot.setNullability();
        }

        for (Iterator it = assns.iterator(); it.hasNext(); ) {
            Association ass = (Association) it.next();
            ass.setNullability();
        }

        for (Iterator it = types.iterator(); it.hasNext(); ) {
            ObjectType ot = (ObjectType) it.next();
            ot.generateUniqueKeys();
        }

        for (Iterator it = assns.iterator(); it.hasNext(); ) {
            Association ass = (Association) it.next();
            ass.generateUniqueKeys();
        }

        for (Iterator it = types.iterator(); it.hasNext(); ) {
            ObjectType ot = (ObjectType) it.next();
            ot.generateForeignKeys();
        }

        for (Iterator it = assns.iterator(); it.hasNext(); ) {
            Association ass = (Association) it.next();
            ass.generateForeignKeys();
        }

        m_generatedTypes.addAll(types);
        m_generatedAssns.addAll(assns);
    }

}
