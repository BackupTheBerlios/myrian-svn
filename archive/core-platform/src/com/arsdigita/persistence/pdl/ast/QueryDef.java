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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.QueryType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Utilities;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Defines a data query that is not associated with an object type or
 * association.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #7 $ $Date: 2002/08/23 $
 */
public class QueryDef extends NamedSQLDef {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/QueryDef.java#7 $ by $Author: randyg $, $DateTime: 2002/08/23 16:33:19 $";

    // this determines if it is a "zero or one row ", "one row" query
    // or a multi-row query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;
    private static int count = 0;
    private static final Logger s_log =
        Logger.getLogger(QueryDef.class.getName());

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
     *  This ensures that all values being returned have a specified
     *  jdbc type.  This is important to avoid ClassCastExceptions
     *  when retrieving these properties
     */
    protected void validateMappings() {
        StringBuffer warning = new StringBuffer();

        for (Iterator it = m_sql.getMapStatements().iterator(); it.hasNext();) {
            String path = ((MapStatement)it.next()).getPrettyPath();
            if (getPropertyDef(path) == null) {
                // Check to see if the property is part of an object type
                if (!(path.indexOf(".") > -1 &&
                      getPropertyDef(path.substring
                                     (0, path.lastIndexOf("."))) != null)) {
                    // if we have something like mapping.template.id we
                    // need to check to make sure that the "mapping" is
                    // property declared and if so, we assume that the
                    // "id" is proper since we are now dealing 
                    // with object types
                    if (!(path.indexOf(".") < path.lastIndexOf(".") &&
                          getPropertyDef(path.substring
                                         (0, path.indexOf("."))) != null)) {
                        warning.append("Property: " + path + 
                                       Utilities.LINE_BREAK);
                    }
                }
            }
        }

        if (warning.length() > 0) {
            count++;
            StringWriter str = new StringWriter();
            PrintWriter msg = new PrintWriter(str);
            msg.println("Warning: The following properties are not declared " +
                        " in the definition of the query and therefore do " +
                        " not have an associated JDBC Type.  Please define " +
                        " the property at the top of the query.  For " +
                        " backwards compatibility we are going to use the " +
                        " default JDBC type as defined by the database " +
                        " (which could lead to errors)." +
                        Utilities.LINE_BREAK +
                        count + "Model: " + getModelDef().getName() + "." + getName() +
                        Utilities.LINE_BREAK +
                        "Query: " + getName());
            msg.println(warning.toString());
            s_log.warn(str.toString());
        }
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
