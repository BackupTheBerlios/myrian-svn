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

package com.arsdigita.persistence.metadata;

import java.io.PrintStream;

/**
 * The Mapping class is used to map from a path into a network of DataTypes to
 * a Column in a particular ResultSet. This allows multiple types to be loaded
 * with a single SQL statement.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

public class Mapping extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/Mapping.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    /**
     * The path into a network of DataTypes.
     **/
    private String[] m_path;

    /**
     * The Column that holds the value for the specified path.
     **/
    private Column m_column;


    /**
     * Constructs a new Mapping with the given path and Column.
     *
     * @param path The path into a network of DataTypes.
     * @param column The column that holds the value for the specified path.
     **/

    public Mapping(String[] path, Column column) {
        if (path == null || path.length == 0) {
            throw new IllegalArgumentException(
                "The path must be non null and non empty."
                );
        }

        if (column == null) {
            throw new IllegalArgumentException(
                "The column must be non null."
                );
        }

        m_path = path;
        m_column = column;
    }


    /**
     * Returns the path for this mapping.
     *
     * @return The path for this mapping.
     **/

    public String[] getPath() {
        return m_path;
    }


    /**
     * Returns the Column for this mapping.
     *
     * @return The Column for this mapping.
     **/

    public Column getColumn() {
        return m_column;
    }

    void outputPDL(PrintStream out) {
        for (int i = 0; i < m_path.length; i++) {
            out.print(m_path[i]);
            if (i < m_path.length - 1) {
                out.print(".");
            }
        }

        out.print(" = ");
        out.print(m_column.getTableName() + "." + m_column.getColumnName());
    }

}
