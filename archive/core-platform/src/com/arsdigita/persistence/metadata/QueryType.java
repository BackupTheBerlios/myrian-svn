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

import com.arsdigita.persistence.PersistenceException;
import java.util.Iterator;
import java.io.PrintStream;

/**
 * The QueryType class is the form of CompoundType used for data queries.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 **/

public class QueryType extends CompoundType {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/QueryType.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";


    // This indicates the limits on the number of rows returned by the query
    private int m_lowerBound = 0;
    private int m_upperBound = Integer.MAX_VALUE;

    /**
     * The event for retrieving the query.
     **/
    private Event m_event;


    /**
     * Constructs a new QueryType with the given name. This QueryType will
     * start out with no properties.
     *
     * @param name The name of the query type.
     * @param event The event used to retrieve the query.
     **/

    public QueryType(String name, Event event) {
        super(name);
        if (event == null) {
            throw new IllegalArgumentException("event must be non null");
        }

        m_event = event;

        initOption("WRAP_QUERIES", Boolean.TRUE);
    }


    /**
     * Returns the event for this QueryType.
     *
     * @return The event for this QueryType.
     **/

    public Event getEvent() {
        return m_event;
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
                                           "must be 0 or 1 [query " + getName() +
                                           "]");
        }
        m_lowerBound = lowerBound;
    }


    /**
     *  This returns the upper bound on the number of rows that can be
     *  returned by this query
     */
    public int getReturnsUpperBound() {
        return m_upperBound;
    }


    /**
     *  This returns the lower bound on the number of rows that can be
     *  returned by this query
     */
    public int getReturnsLowerBound() {
        return m_lowerBound;
    }


    /**
     * Outputs a serialized reperesentation of this QueryType on the given
     * PrintStream.
     *
     * The following format is used:
     *
     * <pre>
     *    "query" &lt;name&gt; "{"
     *        &lt;properties&gt; ";"
     *    "}"
     * </pre>
     *
     * @param out The PrintStream to use for output.
     *
     */
    void outputPDL(PrintStream out) {
        out.print("query " + getName() + " returns " + m_lowerBound + "..");
        if (m_upperBound < Integer.MAX_VALUE) {
            out.print(m_upperBound);
        } else {
            out.print("n");
        }
        out.println(" {");

        outputOptionsPDL(out);

        for (Iterator it = getProperties(); it.hasNext(); ) {
            Element el = (Element) it.next();
            out.print("    ");
            el.outputPDL(out);
            out.println(";");
        }

        for (Iterator it = m_event.getOperations(); it.hasNext(); ) {
            Operation op = (Operation) it.next();
            out.println();
            op.outputPDL(out);
            out.println();
        }

        out.print("}");
    }

}
