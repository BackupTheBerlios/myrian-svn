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

import com.arsdigita.util.Assert;
import java.io.PrintStream;

/**
 * A simple class the encapsulates the relationship between two columns, used
 * to specify a join order. JoinElement is used by JoinPath to specify a
 * complete path from one object type or table to another.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/JoinElement.java#4 $
 * @since 4.6
 **/
public class JoinElement extends Element {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/JoinElement.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";
    private Column m_from = null;
    // column belonging to the start table, or closest to it

    private Column m_to = null;
    // column belonging to the goal table, or closest to it

    /**
     * Creates a new JoinElement from "from" to "to".
     *
     * @param from the column closest to the start table
     * @param to the column closest to the goal table
     *
     * @pre from != null && to != null
     **/
    public JoinElement(Column from, Column to) {
        Assert.assertNotNull(from, "from");
        Assert.assertNotNull(to, "to");

        m_from = from;
        m_to = to;

        setLineInfo(m_from);
    }

    /**
     * Create a new JoinElement, deferring specification of the columns to
     * later.
     **/
    public JoinElement() {}

    /**
     * Returns the "from" column
     *
     * @return the "from" column
     **/
    public Column getFrom() {
        return m_from;
    }

    /**
     * Sets the "from" column.
     *
     * @param from the new "from" column
     * @pre from != null
     **/
    public void setFrom(Column from) {
        m_from = from;
        setLineInfo(m_from);
    }

    /**
     * Returns the "to" column
     *
     * @return the "to" column
     **/
    public Column getTo() {
        return m_to;
    }

    /**
     * Sets the "to" column
     *
     * @param to the new "to" column
     * @pre to != null
     **/
    public void setTo(Column to) {
        m_to = to;
    }


    /**
     * Outputs a serialized representation of this JoinElement.
     *
     * @param out The PrintStream to use for output.
     **/

    void outputPDL(PrintStream out) {
        out.print("join " + m_from.getTableName() + "." +
                  m_from.getColumnName() + " to " +
                  m_to.getTableName() + "." + m_to.getColumnName());
    }

    /**
     * Is this JoinElement equivalent to another JoinEleemnt?
     *
     * @param object the object to compare
     * @return true if the object is equal to this JoinElement, false otherwise
     **/
    public boolean equals(Object object) {
        if ( null == object ) {
            return false;
        }
        if (object instanceof JoinElement) {
            if ( this == object ) {
                return true;
            }

            JoinElement je = (JoinElement)object;

            return m_from.equals(je.getFrom()) && m_to.equals(je.getTo());
        }

        return false;
    }

    /**
     * Returns a hashcode for this joinelement.
     *
     * @pre getTo() != null && getFrom() != null
     * @return a hashcode for this joinelement
     **/
    public int hashCode() {
        int result = 17;
        result = 37*result + m_from.hashCode();
        result = 37*result + m_to.hashCode();
        return result;
    }



}
