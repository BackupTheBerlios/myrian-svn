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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.Utilities;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;


/**
 * Defines a data query that is not associated with an object type or 
 * association.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public abstract class NamedSQLDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/NamedSQLDef.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    // the query name
    protected String m_name;

    // the PropertyDefs that this query contains
    protected Map m_attrs = new HashMap();

    // the SQLBlock that this query wraps
    protected SQLBlockDef m_sql;

    // The options for this sql block.
    protected OptionBlock m_options = null;

    /**
     * Create a new NamedSQLDef named "name"
     *
     * @param name query name
     */
    public NamedSQLDef(String name) {
        m_name = name;
    }

    /**
     * Returns the NamedSQLDef's name
     *
     * @return the NamedSQLDef's name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns a named propertydef, or null.    
     * 
     * @param name the name of the propertydef to return
     * @return a named propertydef, or null.
     */
    public PropertyDef getPropertyDef(String name) {
        return (PropertyDef)m_attrs.get(name);
    }

    /**
     * Add a PropertyDef to this query
     *
     * @param pd the PropertyDef to add
     */
    public void add(PropertyDef pd) {
        m_attrs.put(pd.getName(), pd);
        super.add(pd);
    }

    /**
     * Set the SQLBlock for this query
     *
     * @param block the SQLBlockDef to add
     */
    public void add(SQLBlockDef block) {
        m_sql = block;
        super.add(m_sql);
    }


    /**
     * Adds an option block to this NamedSQLDef
     **/

    public void add(OptionBlock options) {
	m_options = options;
    }

    /**
     * Check that the NamedSQLDef is in a valid state.  A valid state is when
     * all of its PropertyDefs and its SQLBlockDef are valid.
     */
    void validate() {
        validate(m_attrs);
        validate(m_sql);
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("sql " + m_name + " {" + Utilities.LINE_BREAK);

        for (Iterator it = m_attrs.values().iterator(); it.hasNext(); ) {
            result.append("    " + it.next() + ";" + Utilities.LINE_BREAK);
        }

        result.append(Utilities.LINE_BREAK + m_sql);

        result.append(Utilities.LINE_BREAK + "}");

        return result.toString();
    }

}
