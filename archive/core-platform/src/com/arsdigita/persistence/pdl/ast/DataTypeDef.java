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

package com.arsdigita.persistence.pdl.ast;

import java.util.HashMap;
import java.sql.Types;

/**
 * Defines a database datatype.  It contains the datatype name, and optionally
 * a size (such as the 400 in varchar(400)).
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */
public class DataTypeDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/DataTypeDef.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    // the type name
    private String m_type;

    // the size.  -1 for no size
    private int m_size;

    // mapping of written database types to integer type codes
    private static HashMap s_dbTypes = new HashMap();
    // For deprecated types.
    private static HashMap s_oldDbTypes = new HashMap();

    // not a comprehensive list by any means, but I think this covers
    // everything we regularly use
    static {
        s_oldDbTypes.put("integer", new Integer(Types.INTEGER));
        s_oldDbTypes.put("clob", new Integer(Types.CLOB));
        s_oldDbTypes.put("blob", new Integer(Types.BLOB));
        s_oldDbTypes.put("bit", new Integer(Types.BIT));
        s_oldDbTypes.put("char", new Integer(Types.CHAR));
        s_oldDbTypes.put("date", new Integer(Types.DATE));
        s_oldDbTypes.put("number", new Integer(Types.DECIMAL));
        s_oldDbTypes.put("float", new Integer(Types.FLOAT));
        s_oldDbTypes.put("varchar", new Integer(Types.VARCHAR));

        s_dbTypes.put("INTEGER", new Integer(Types.INTEGER));
        s_dbTypes.put("CLOB", new Integer(Types.CLOB));
        s_dbTypes.put("BLOB", new Integer(Types.BLOB));
        s_dbTypes.put("BIT", new Integer(Types.BIT));
        s_dbTypes.put("CHAR", new Integer(Types.CHAR));
        s_dbTypes.put("DATE", new Integer(Types.DATE));
        s_dbTypes.put("TIMESTAMP", new Integer(Types.TIMESTAMP));
        s_dbTypes.put("DECIMAL", new Integer(Types.DECIMAL));
        s_dbTypes.put("FLOAT", new Integer(Types.FLOAT));
        s_dbTypes.put("VARCHAR", new Integer(Types.VARCHAR));
        s_dbTypes.put("NUMERIC", new Integer(Types.NUMERIC));
    }

    /**
     * Create a new DataTypeDef with the given type and size.
     *
     * @param type the datatype
     * @param size the size, -1 for no size
     * @pre type != null
     */
    public DataTypeDef(String type, int size) {
        m_type = type;
        m_size = size;
    }

    /**
     * Create a new DataTypeDef with the given type and no size.
     *
     * @param type the datatype
     * @pre type != null
     */
    public DataTypeDef(String type) {
        this(type, -1);
    }

    /**
     * Returns the name of this datatype.
     *
     * @return the name of this datatype
     */
    public String getName() {
        if ( m_size < 0 ) {
            return m_type;
        } else {
            return m_type + "(" + m_size + ")";
        }
    }

    public int getSize() {
        return m_size;
    }

    public int getTypeCode() {
        Integer typeCode = null;
        if (s_dbTypes.containsKey(m_type)) {
            typeCode = (Integer)s_dbTypes.get(m_type);
        } else if (s_oldDbTypes.containsKey(m_type.toLowerCase())) {
            typeCode = (Integer) s_oldDbTypes.get(m_type.toLowerCase());
            warn("Deprecated type name: '" + m_type + "'\n    " +
                 "Please choose from the following set of types: " +
                 s_dbTypes.keySet());
        } else {
            error("Type " + m_type + " not found");
        }

        return typeCode.intValue();
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        return getName();
    }

}
