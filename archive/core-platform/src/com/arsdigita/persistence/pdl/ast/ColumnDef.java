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

import com.arsdigita.persistence.metadata.Column;

import java.sql.Types;

/**
 * Defines a database column, including the table name, column name, and 
 * data type.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public class ColumnDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/ColumnDef.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    // the name of the column
    private String m_column;

    // the name of the table
    private String m_table;

    // the type of the column
    private DataTypeDef m_type = null;

    /**
     * Create a new ColumnDef for the specified table and column.
     *
     * @param table the table name 
     * @param column the column name
     * @param type the column type
     */
    public ColumnDef(String table, String column, DataTypeDef type) {
        m_column = column;
        m_table = table;
        m_type = type;
    }

    /**
     * Create a new ColumnDef for the specified table and column.
     *
     * @param table the table name 
     * @param column the column name
     */
    public ColumnDef(String table, String column) {
        this(table, column, null);
    }

    /**
     * Get the column name.
     *  
     * @return the column name (and table if applicable)
     */
    public String getName() {
        return m_table + "." + m_column;
    }

    /**
     * Generates the Column that this ColumnDef represents.
     *
     * @returns the Column that this ColumnDef represents.
     */
    public Column generateLogicalModel() {
        if (m_type != null) {
            return new Column(m_table, m_column, m_type.getTypeCode(),
                              m_type.getSize());
        } else {
            return new Column(m_table, m_column);
        }
    }

    /**
     * Generates the Column that this ColumnDef represents.
     *
     * @returns the Column that this ColumnDef represents.
     */
    public Column generateLogicalModel(int defaultJDBCType) {
        if (m_type != null) {
            return new Column(m_table, m_column, m_type.getTypeCode(),
                              m_type.getSize());
        } else {
            return new Column(m_table, m_column, defaultJDBCType);
        }
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
