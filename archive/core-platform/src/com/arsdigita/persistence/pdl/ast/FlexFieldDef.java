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


/**
 * Defines a FlexField column in the database associated with a particular
 * object type.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public class FlexFieldDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/FlexFieldDef.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    // the Column to specify as a flexfield
    private ColumnDef m_column;

    /**
     * Create a new FlexFieldDef from a column and type
     *
     * @param column the column to use as a flexfield
     * @param type the datatype of the column
     */
    public FlexFieldDef(ColumnDef column) {
        m_column = column;

        add(m_column);
    }

    /**
     * Get the name of this flexfield
     *
     * @return the name of this flexfield
     */
    public String getName() {
        return m_column.toString();
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
