/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.metadata;

import java.lang.StringBuffer;
import java.sql.Types;
import java.util.List;
import java.util.Iterator;

/**
 * General static utility methods for the metadata classes. These
 * methods are not intended to be used outside of the metadata
 * package.
 *
 * @since 2001-04-02
 * @version 1.0
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 **/
public class Utilities  {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Utilities.java#5 $ by $Author: richardl $, $DateTime: 2002/10/16 14:39:19 $";

    public final static String LINE_BREAK =
        System.getProperty("line.separator", "\n\r");

    /**
     * It makes neo sense to instantiate this Utilities class. Hide
     * the constructor.
     **/
    private Utilities() {}

    /**
     *  This takes a string buffer and returns the same thing that was
     *  passed in if the value is not null or creates a StringBuffer to
     *  return if the value is null.
     *
     *  @param sb The StringBuffer to examine
     *  @return A non-null StringBuffer
     */
    public static final StringBuffer getSB(StringBuffer sb) {
        if (sb == null) {
            return new StringBuffer();
        } else {
            return sb;
        }
    }

    /**
     *  This function returns true if the value <code>type</code>
     *  maps to a valid JDBC Type. Mainly intended for use in
     *  precondition statements.
     *
     *  @param type The value to check
     *  @return true if type is one of the constants in <code>java.sql.Types</code>
     *
     */
    public static boolean isJDBCType(final int type) {
        // Kinda ugly to have one big case, but it's actually the
        // fastest lookup.
        switch (type) {
        case Types.ARRAY:
        case Types.BIGINT:
        case Types.BINARY:
        case Types.BIT:
        case Types.BLOB:
        case Types.CHAR:
        case Types.CLOB:
        case Types.DATE:
        case Types.DECIMAL:
        case Types.DISTINCT:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.JAVA_OBJECT:
        case Types.LONGVARBINARY:
        case Types.LONGVARCHAR:
        case Types.NULL:
        case Types.NUMERIC:
        case Types.OTHER:
        case Types.REAL:
        case Types.REF:
        case Types.SMALLINT:
        case Types.STRUCT:
        case Types.TIME:
        case Types.TIMESTAMP:
        case Types.TINYINT:
        case Types.VARBINARY:
        case Types.VARCHAR:
            return true;
        default:
            return false;
        }

    }

    /**
     * Returns the Property that composes the Object Key.
     *
     * @param type the ObjectType to get the ObjectKey from
     * @return the Property that composes the Object Key
     */
    protected static Property getKeyProperty(ObjectType type) {
        Iterator key = type.getKeyProperties();

        if (!key.hasNext()) {
            if (type.getSupertype() == null) {
                //                s_log.warn(type.getName() + " has no object key defined!");
            }

            return null;
        }

        Property prop = (Property)key.next();

        if (prop.getColumn() == null) {
            boolean error = false;
            Iterator iter = type.getProperties();

            while (iter.hasNext()) {
                prop = (Property)iter.next();

                if (prop.isAttribute() && (prop.getColumn() != null)) {
                    error = true;
                }
            }

            if (error) {
                //                s_log.warn(type.getName() + " does not have a key column " +
                //                           "defined even though some properties do have " +
                //                           "columns specified.");
            }

            return null;
        }

        if (key.hasNext()) {
            return null;
        }

        return prop;
    }


    /**
     * "join" a List of Strings into a single string, with each string
     * separated by a defined separator string.
     * @deprecated use {@link com.arsdigita.util.StringUtils}
     *
     * @param elements the strings to join together
     * @param sep the separator string
     * @return the strings joined together
     */
    public static String join(List elements, String sep) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator iter = elements.iterator();

        while (iter.hasNext()) {
            String element = (String)iter.next();

            if (!first) {
                sb.append(sep);
            } else {
                first = false;
            }

            sb.append(element);
        }

        return sb.toString();
    }
}
