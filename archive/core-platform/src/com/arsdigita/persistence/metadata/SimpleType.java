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

import java.io.PrintStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Collection;

/**
 * The SimpleType class is the base class for all the primative DataTypes
 * that the perisistence layer knows how to store. These simple types serve as
 * the atoms from which compound types are built.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

abstract public class SimpleType extends DataType {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/SimpleType.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private int m_JDBCtype;
    private Class m_class;

    /**
     * Constructs a new SimpleType with the given name.
     **/
    public SimpleType(String name, Class klass, int JDBCtype) {
        super(name);
        m_class = klass;
        m_JDBCtype = JDBCtype;
    }

    /**
     * Returns false.
     *
     * @return false
     **/

    public boolean isCompound() {
        return false;
    }

    /**
     * Outputs a serialized representation of this SimpleType on the given
     * PrintStrem.
     *
     * The format used is the following:
     *
     * <pre>
     *     &lt;name&gt;
     * </pre>
     *
     * @param out The PrintStream to use for output.
     **/

    public void outputPDL(PrintStream out) {
        out.print(getName());
    }


    /**
     *  this returns the JDBC type for this SimpleType
     *  The JDBCType is a constant as specified by java.sql.Types
     */
    public int getJDBCtype() {
        return m_JDBCtype;
    }

    /**
     * Returns the java Class object corresponding to this SimpleType.
     *
     * @return A Java Class object.
     **/
    public Class getJavaClass() {
        return m_class;
    }

    public boolean needsRefresh(Object value, int jdbcType) {
        return false;
    }

    public void doRefresh(ResultSet rs, String column, Object value)
        throws SQLException {
        // Do nothing by default.
    }

    public String getLiteral(Object value, int jdbcType) {
        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            int size = coll.size();
            StringBuffer result = new StringBuffer(3*size + 2);

            result.append("(");

            for (int i = 0; i < size - 1; i++) {
                result.append("?, ");
            }

            if (size > 0) {
                result.append("?");
            }

            result.append(")");

            return result.toString();
        } else {
            return "?";
        }
    }

    public int bindNull(PreparedStatement ps, int index,
                        int jdbcType) throws SQLException {
        ps.setNull(index, jdbcType);
        return 1;
    }

    public int bindCollection(PreparedStatement ps, int index,
                              Collection values, int jdbcType)
        throws SQLException {
        int result = 0;

        for (Iterator it = values.iterator(); it.hasNext(); ) {
            Object value = it.next();
            result += bindValue(ps, index + result, value, jdbcType);
        }

        return result;
    }

    public abstract int bindValue(PreparedStatement ps, int index,
                                  Object value, int jdbcType)
        throws SQLException;

    public int bind(PreparedStatement ps, int index, Object value,
                    int jdbcType)
        throws SQLException {
        if (value == null) {
            return bindNull(ps, index, jdbcType);
        } else if (value instanceof Collection) {
            return bindCollection(ps, index, (Collection) value, jdbcType);
        } else {
            return bindValue(ps, index, value, jdbcType);
        }
    }

    public abstract Object fetch(ResultSet rs, String column)
        throws SQLException;

}
