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
import com.arsdigita.persistence.metadata.Table;
import com.arsdigita.persistence.metadata.Column;
import com.arsdigita.persistence.Utilities;

import java.sql.Types;
import org.apache.log4j.Logger;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Defines a database column, including the table name, column name, and
 * data type.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Revision: #8 $ $Date: 2002/08/26 $
 */
public class ColumnDef extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/ColumnDef.java#8 $ by $Author: rhs $, $DateTime: 2002/08/26 17:54:19 $";

    private static int count = 0;
    private static final Logger s_log =
        Logger.getLogger(ColumnDef.class.getName());

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

    String getTable() {
        return m_table;
    }

    String getColumn() {
        return m_column;
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
    public Column generateLogicalModel(int defaultJDBCType) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        Column result;

        Table table = root.getTable(m_table);
        if (table == null) {
            table = new Table(m_table);
            initLineInfo(table);
            root.addTable(table);
        }

        result = table.getColumn(m_column);

        if (m_type != null) {
            if (result == null) {
                result = new Column(table, m_column, m_type.getTypeCode(),
                                    m_type.getSize());
            } else {
                if (result.getType() == Integer.MIN_VALUE) {
                    result.setType(m_type.getTypeCode());
                } else if (result.getType() != m_type.getTypeCode()) {
                    error("Column type definition conflicts " +
                          "with previous definition: " + result.getLocation());
                }

                if (result.getSize() < 0) {
                    result.setSize(m_type.getSize());
                } else if (result.getSize() != m_type.getSize()) {
                    error("Column type definition conflicts " +
                          "with previous definition: " + result.getLocation());
                }
            }
        } else {
            if (result == null) {
                result = new Column(table, m_column, defaultJDBCType);
            } else {
                if (result.getType() == Integer.MIN_VALUE &&
                    defaultJDBCType != Integer.MIN_VALUE) {
                    result.setType(defaultJDBCType);
                } else if (defaultJDBCType != Integer.MIN_VALUE &&
                           result.getType() != defaultJDBCType) {
                    error("Column type definition conflicts " +
                          "with previous definition: " + result.getLocation());
                }
            }
        }

        initLineInfo(result);

        return result;
    }

    /**
     * Generates the Column that this ColumnDef represents.
     *
     * @returns the Column that this ColumnDef represents.
     */
    public Column generateLogicalModel() {
        return generateLogicalModel(Integer.MIN_VALUE);
    }

    void validate() {
        validate("");
    }

    /**
     *  This validates that the column has the correct properties,
     *  including a jdbc type that was set by the PDL.
     *
     *  @param beginningMessage This value will show up in the message
     *  at the beginning of the description to help users debug.  This
     *  is used, for instance, by PropertyDef to pass in the name of the
     *  object type as well as the actual property.
     */
    void validate(String beginningMessage) {
        if (m_type == null) {
            count++;
            if (beginningMessage == null) {
                beginningMessage = "";
            }
            String warning =
                ("Warning: The following Column does not have a SQL " +
                 " type specified.  For backwards compatibility, we " +
                 " are going to try to guess the type.  The type " +
                 " should be added as soon as possible. " +
                 Utilities.LINE_BREAK +
                 beginningMessage + count + "Column: " + getName());
            s_log.warn(warning);
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
