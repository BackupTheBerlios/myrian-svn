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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.QueryType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Utilities;

import java.util.Iterator;

/**
 * Defines a data query that is not associated with an object type or 
 * association.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/18 $
 */
public class QueryDef extends NamedSQLDef {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/QueryDef.java#3 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    // this determines if it is a "zero or one row ", "one row" query 
    // or a multi-row query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    /**
     * Create a new QueryDef named "name"
     *
     * @param name query name
     */
    public QueryDef(String name) {
        super(name);
    }


    /**
     *  This sets the upper bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsUpperBound(int upperBound) {
        m_upperBound = upperBound;
    }

    
    /**
     *  This sets the lower bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsLowerBound(int lowerBound) {
        if (lowerBound > 1 || lowerBound < 0) {
            throw new PersistenceException("The lower bound for a given query " +
                                           "must be 0 or 1 [query " + m_name +
                                           "]");
        }
        m_lowerBound = lowerBound;
    }



    /**
     * Creates the metadata for this QueryDef.
     */
    QueryType generateEvents() {
        Event event = new Event();
        initLineInfo(event);

        Operation op = m_sql.generateOperation();
        event.addOperation(op);

        QueryType result =  new QueryType(m_name, event);
        initLineInfo(result);
        result.setReturnsLowerBound(m_lowerBound);
        result.setReturnsUpperBound(m_upperBound);

        for (Iterator it = m_attrs.values().iterator(); it.hasNext(); ) {
            PropertyDef pd = (PropertyDef) it.next();
            result.addProperty(pd.generateLogicalModel());
        }

        for (Iterator it = op.getMappings(); it.hasNext(); ) {
            Mapping mapping = (Mapping) it.next();
            String[] path = mapping.getPath();
            if (!result.hasProperty(path[0])) {
                Property prop = new Property(path[0], MetadataRoot.OBJECT);
                initLineInfo(prop);
                result.addProperty(prop);
            }
        }

        if (m_options != null) {
            m_options.setOptions(result);
        }

        return result;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("query " + m_name + " {" + Utilities.LINE_BREAK);

        for (Iterator it = m_attrs.values().iterator(); it.hasNext(); ) {
            result.append("    " + it.next() + ";" + Utilities.LINE_BREAK);
        }

        result.append(Utilities.LINE_BREAK + m_sql);

        result.append(Utilities.LINE_BREAK + "}");

        return result.toString();
    }

}
