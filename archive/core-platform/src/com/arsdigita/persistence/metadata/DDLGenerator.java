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

import java.util.Collection;
import java.util.Map;

/**
 * A interface that defines an API to automatically generate DDL
 * statements based on the information passed in.  The primary use for
 * this class is to provide DDL to create and alter tables used by
 * {@link com.arsdigita.persistence.metadata.DynamicObjectType}.
 *
 * Note that the DDLGenerator does not support dropping tables and 
 * columns.  This is to avoid data loss and allow rolling back of UDCT
 * operations.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @author <a href="mailto:randyg@arsdigita.com">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGenerator.java#3 $
 * @since 4.6.3 */

public interface DDLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DDLGenerator.java#3 $ by $Author: randyg $, $DateTime: 2002/07/30 10:05:06 $";
    /**
     * This takes an ObjectType name and model, and generates a unique
     * table name that can be used to store the object type.
     *
     * @param model the object type's model
     * @param name the object type's name
     * @return a unique table name that is used to store data about about
     *         object type
     */
    String generateTableName(String model, String name);

    /**
     * This takes a table name and a Property to generate a unique 
     * column name.
     * 
     * @param objectType the objecttype that will contain the property 
     * @param proposedName the proposed name of the property
     * @return a unique column name that will be used to store this property
     */
    String generateColumnName(ObjectType objectType, String proposedName);

    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a "create table" or an "alter
     * table" statement to add the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param keyColumn the key column of the object type
     * @param properties additional properties to add
     * @param defaultValueMap mapping from property name to default value
     * @return a DDL statement to create or alter the object type table
     */
    Collection generateTable(ObjectType type,
                             Column keyColumn,
                             Collection properties,
                             Map defaultValueMap);

    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a "create table" or an "alter
     * table" statement to add the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param properties additional properties to add
     * @param defaultValueMap mapping from property name to default value
     * @return a DDL statement to create or alter the object type table
     */
    Collection generateTable(ObjectType type,
                             Collection properties,
                             Map defaultValueMap);

    /**
     * Takes an object type, a primary key property, and a collection of
     * additional properties.  Returns either a "create table" or an "alter
     * table" statement to add the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param keyColumn the key column of the object type
     * @param properties additional properties to add
     * @return a DDL statement to create or alter the object type table
     */
    Collection generateTable(ObjectType type,
                             Column keyColumn,
                             Collection properties);

    /**
     * Takes an object type and a collection of additional properties. 
     * Returns either a "create table" or an "alter table" statement to add
     * the properties to the object type's table.
     *
     * @param type the ObjectType
     * @param properties additional properties to add
     * @return a DDL statement to create or alter the object type table
     */
    Collection generateTable(ObjectType type,
                             Collection properties);

    /**
     * Determines a unique name for a mapping table for a particular 
     * role reference and object type.
     * 
     * @param type the objecttype that owns the property
     * @param name the property name
     * @return a unique name for a mapping table
     */
    String generateMappingTableName(ObjectType type, String name);

    /**
     * Creates the DDL to generate a mapping table from one object type to
     * another.
     *
     * @param type the dynamically generated object type
     * @param property the role reference
     * @return the DDL statement to create the mapping table
     */
    String generateMappingTable(ObjectType type, 
                                Property property);

    /**
     * Database systems have varying restrictions on the length of column names.
     * This method obtains the max length for the particular implementation.
     *
     * @return The maximum length
     * @post return > 0
     */
    int getMaxColumnNameLength();
}
