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

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.db.Sequences;
import com.arsdigita.initializer.Startup;
import java.lang.StringBuffer;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * DynamicObjectType is a class that can be used to dynamically
 * create and modify {@link
 * com.arsdigita.persistence.metadata.ObjectType}.  It can be used to
 * create the subtype, add and remove Attributes and RoleReferences as
 * well as perform many other tasks related to the new object type.
 * When the application is done creating the object type, it should
 * call {@link #save()} to persist the information about the newly created
 * object type.  
 *
 * @deprecated Use com.arsdigita.metadata.DynamicObjectType instead.
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/30 $ 
 */

public class DynamicObjectType {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/DynamicObjectType.java#3 $ by $Author: randyg $, $DateTime: 2002/07/30 10:05:06 $";

    // The DDL generator used
    private static DDLGenerator m_generator = DDLGeneratorFactory.getInstance();

    // the contained ObjectType
    private ObjectType m_objectType;

    private static final String objectTypeString = 
                               "com.arsdigita.persistence.DynamicObjectType";

    // This is used for updating any information about the given DataObject.
    private DataObject m_dataObject;

    // this is a convenience member variable
    private String m_tableName = null;

    // this is the string buffer that is used to hold the
    // DDL statement
    private StringBuffer m_ddlToAdd = null;

    // this shows if the item is new or not which is used to
    // determine if the ddlToAdd should be wrapped with a 
    // "create table" or an "alter table"
    private boolean m_isNew = true;

    private Collection m_mappingTables = new ArrayList();

    private Collection m_newProperties = new ArrayList();

    private Map m_defaultValueMap = new HashMap();

    private Column m_keyColumn = null;



    /*  TODO
        Items for docs:
        1. The name should be short
    */
    /*  TODO
      Items needed that we do not yet have:
      3. We need to force the user to specify a Refernce Key
      4. We need to restrict the possible parent types to types
         with only a single key
      5. When adding a RoleReference to each object type,
         we need to make sure to not create the mapping table twice
     */


    /**
     *  This procedures allows developers to dynamically create 
     *  object types that subtype existing object types.
     *  The model.name string must be unique
     *
     *  @param supertype This is the existing ObjectType that should
     *                   be extended to create this ObjectType.
     *                   If the supertype is a DynamicObjectType,
     *                   it must be saved before saving this new
     *                   DynamicObjectType.  A supertype can not be null
     *  @param name This is the name of the new object type.  This
     *              should only be the name and should not contain
     *              and "."  The fully qualified name (which is
     *              required by the other constructor) is the 
     *              model of the supertype followed by a "."
     *              followed by the passed in name.  This can 
     *              be retrieved by calling 
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}.
     *              The name must not be null.
     *
     *  @pre name != null
     *  @pre supertype != null
     */
    public DynamicObjectType(String name, ObjectType supertype) {
        this(name, supertype, supertype.getModel());
    }


    /**
     *  This procedures allows developers to dynamically create object
     *  types that may or may not subtype an object.  The
     *  model.name string must be unique and either the model or the
     *  supertype must not be null.  If the model is null, the model
     *  from the supertype is used.  
     *
     *  @param supertype This is the existing ObjectType that should
     *                   be extended to create this ObjectType.
     *                   If the supertype is a DynamicObjectType,
     *                   it must be saved before saving this new
     *                   DynamicObjectType.  A supertype can be null.
     *  @param name This is the name of the new object type.  This
     *              should only be the name and should not contain
     *              and "."  The fully qualified name (which is
     *              required by the other constructor) is the 
     *              model of the supertype followed by a "."
     *              followed by the passed in name.  This can 
     *              be retrieved by calling 
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}.
     *              The name must not be null and must only contain
     *              alpha-numeric characters.
     *  @param model This is the name of the model that will be used
     *               for the object type.  If this is specified, it is used
     *               as the object type.
     *
     *  @pre name != null
     *  @pre supertype != null || model != null 
     */
    public DynamicObjectType(String name, ObjectType supertype, Model model) {
        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                throw new PersistenceException
                    ("The name of the DynamicObjectType must be alphanumeric. " +
                     "You tried to create it using [" + name + "]");
            }
        }
        m_objectType = new ObjectType(name, supertype);

        if (model == null) {
            model = supertype.getModel();
        }
        model.addDataType(m_objectType);

        // set up the table name and the reference key
        m_tableName = m_generator.generateTableName(
                                    m_objectType.getModel().getName(), 
                                    name);

        // now we try to create a reference key 
        String columnName = m_objectType.getName() + "_id";
        Column key = new Column(m_tableName, columnName,
                                java.sql.Types.INTEGER, 32);
        m_keyColumn = key;

        if (supertype != null) {
            m_objectType.setReferenceKey(key);
        } else {
            // in this case, there is not parent type so we have to
            // create the primary key
            String propertyName = "id";
            Property property = new Property(propertyName, 
                                             MetadataRoot.BIGDECIMAL, 
                                             Property.REQUIRED);
            property.setColumn(key);
            m_objectType.addProperty(property);
            m_objectType.addKeyProperty(propertyName);
        }
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param typeName The name of the object type to instantiate.
     *                  This needs to be the fully qualified name
     *                  such as "com.arsdigita.cms.metadata.Hotel"
     *                  This is the same name that is returned by
     *                  calling 
     *  {@link com.arsdigita.persistence.metadata.DataType#getQualifiedName()}
     *
     *  @exception PersistenceException thrown if the requested
     *             type cannot be found or has not yet been saved
     */
    public DynamicObjectType(String typeName) {
        this(MetadataRoot.getMetadataRoot().getObjectType(typeName), typeName);
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param objectType The object type that should be mutated
     *
     *  @exception PersistenceException is thrown if passed in ObjectType
     *             is not mutable
     */
    public DynamicObjectType(ObjectType objectType) {
        this(objectType, objectType.getQualifiedName());
    }


    /**
     *  This allows the programmer to instantiate an already existing
     *  dynamic object type.  Specifically, if a content manager
     *  has created an object type and later wants to go back and
     *  edit/add the object type definition.  This does NOT allow
     *  developers to instantiate statically defined ObjectTypes
     *  (e.g. com.arsdigita.kernel.User).
     *
     *  @param objectType The object type that should be mutated
     *
     *  @exception PersistenceException is thrown if passed in ObjectType
     *             is not mutable
     */
    private DynamicObjectType(ObjectType objectType, String typeName) {
        if (objectType == null) {
            throw new PersistenceException("The Object Type you have " +
                                           "requested (" + typeName +
                                           ") does not exist");
        }

        m_objectType = objectType;

        // get the DataObject from the DataBase to make sure that it is
        // a modifiable type.
        DataCollection collection = SessionManager.getSession()
                                                  .retrieve(objectTypeString);
        collection.addEqualsFilter("dynamicType", typeName);

        try {
            if (!collection.next()) {
                throw new PersistenceException(
                            "The Object Type you have requested (" + typeName +
                            ") cannot be used as a Dynamic Object because it " +
                            "has been defined as read-only");
            }

            m_dataObject = collection.getDataObject();

        } finally {
            collection.close();
        }

        Column referenceColumn = m_objectType.getReferenceKey();
        if (referenceColumn != null) {
            m_tableName = referenceColumn.getTableName();
        } else {
            m_tableName = ((Property)m_objectType.getKeyProperties().next())
                .getColumn().getTableName();
        }
        m_isNew = false;
    }


    /**
     *  This adds an Attribute of multiplicity 0..1 which is the
     *  equivalent to adding a column to a table without a "not null"
     *  constraint
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in 
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a 
     *              Clob is used.  Otherwise, a varchar is used.
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    public Property addOptionalAttribute(String name, SimpleType propertyType) {
        return addOptionalAttribute(name, propertyType, -1);
    }


    /**
     *  This adds an Attribute of multiplicity 0..1 which is the
     *  equivalent to adding a column to a table without a "not null"
     *  constraint
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in 
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a 
     *              Clob is used.  Otherwise, a varchar is used.
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    public Property addOptionalAttribute(String name, SimpleType propertyType,
                                         int size) {
        return addAttribute(name, propertyType, Property.NULLABLE, size, null);
    }


    /**
     *  This adds an Attribute of multiplicity 1..1 which is the
     *  equivalent to adding a column to a table with a "not null"
     *  constraint
     *
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in 
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param defaultValue This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type or the default
     *             value is null
     */
    public Property addRequiredAttribute(String name, SimpleType propertyType, 
                                         Object defaultValue) {
        return addRequiredAttribute(name, propertyType, -1, defaultValue);
    }


    /**
     *  This adds an Attribute of multiplicity 1..1 which is the
     *  equivalent to adding a column to a table with a "not null"
     *  constraint
     *
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in 
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param size This is the size of the attribute.  This is an
     *              optional argument but is important for Strings.
     *              Specifically, if the String size > 4000 then a 
     *              Clob is used.  Otherwise, a varchar is used.
     *  @param default This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type or the provided 
     *             default is null.
     */
    public Property addRequiredAttribute(String name, SimpleType propertyType,
                                         int size, Object defaultValue) {
        if (defaultValue == null) {
            throw new PersistenceException
                ("In order to create a required attribute, the default value " +
                 "must not be null");
        }

        return addAttribute(name, propertyType, Property.REQUIRED, size, 
                            defaultValue);
    }
    
    
    /**
     *  This actually adds the attribute to this object type by
     *  creating the actual Attribute object for use by the supertype
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *
     *  @param name The name of the new attribute.  It must be unique
     *              out of the set of current attributes and rolereferences
     *              within this object type
     *  @param propertyType The type of the Property.  This should be
     *                      one of the SimpleTypes specified in 
     *         {@link com.arsdigita.persistence.metadata.MetadataRoot}
     *  @param defaultValue This is the default value for this column.  This
     *                 is required to be "not null" because it is used to
     *                 fill in the values for any rows already in the table.
     *                 Due to limitations in the system, however, you must
     *                 still set the value on new rows so that you avoid
     *                 the "not null" constraint violation
     *  @return This returns the Attribute that has been added to this
     *          DynamicObjectType
     *  @exception PersistenceException if the name is
     *             already in use for this object type
     */
    private Property addAttribute(String name, SimpleType propertyType, 
                                   int multiplicity, int size, 
                                  Object defaultValue) {
        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                throw new PersistenceException
                    ("The name of the DynamicObjectType must be alphanumeric. " +
                     "You tried to create it using [" + name + "]");
            }
        }

        // let's check to see if there is a property of the
        // same name
        boolean hasProperty = m_objectType.hasProperty(name);
        if (!hasProperty) {
            // let's check the properties not looking at case
            Iterator iter = m_objectType.getProperties();
            while (iter.hasNext() && !hasProperty) {
                hasProperty = name.toLowerCase().equals
                    (((Property)iter.next()).getName().toLowerCase());
            }
        }

        if (hasProperty) {
            throw new PersistenceException("The property [" + name + "] already " +
                                           "exists in this object type.");
        }

        Property property = new Property(name, propertyType, multiplicity);
        m_objectType.addProperty(property);

        String columnName = m_generator.generateColumnName(m_objectType, name);

        if (size <= 0) {
            size = -1;
        }

        property.setColumn(new Column(m_tableName, columnName,
                                      propertyType.getJDBCtype(), size));

        m_newProperties.add(property);

        if (defaultValue != null) {
            m_defaultValueMap.put(name, defaultValue);
        } 

        return property;
    }


    /**
     *  This removes an attribute from object type definition.
     *  This can only remove attributes from the dynamic definition
     *  and not from the parent defintion.  That is, the attribute
     *  that should be removed should be found in <code>getAttributes()</code>
     *  <p>
     *  This does not remove any information from the database.
     *  Rather, it simply dereferences the column in the object type.
     *  Therefore, if an attribute is removed, the data can still be
     *  recovered
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *       
     *  @param name the name of the attribute to remove
     *  @exception ModelException if the name passed
     *             in is not an Attribute that can be removed
     */
    public void removeAttribute(String name) {
    // XXX: set a flag here to say that save() needs to regenerate all subtypes
    //      as well
        Property property = m_objectType.getDeclaredProperty(name);
        if (property == null) {
            // we are going to throw an error so let's figure out which one
            if (m_objectType.getProperty(name) != null) {
                throw new PersistenceException
                    ("The property [" + name + "] is a property of the super " +
                     "type and not of this type.  Please delete it from the " +
                     "super type.");
            } else {
                throw new PersistenceException
                    ("The property [" + name + "] you have asked to remove is " +
                     "not a property of this DynamicObjectType");
            }
        } else {
            m_objectType.removeProperty(property);
        }
    }
    


    /**
     *  This adds an association between this DynamicObjectType
     *  and the passed in object type.  The only way to retrieve 
     *  information about the RoleReference is to call 
     *  <code>get(name)</code> using a DataObject of this 
     *  DynamicObjectType.  
     *  <p>
     *  You must call {@link #save()} for the changes to be permanent
     *  <font color="red">This will change when the metadata changes</font>
     *
     *  @param name This is the string that is used to identify this
     *              association.  It must be unique to all of the names
     *              within this object type.
     *  @param objectType This is the ObjectType with which to create
     *                    the association
     *  @exception PersistenceException if the name passed
     *             in is not unique
     */
    /*    public RoleReference addRoleReference(String name, ObjectType objectType) {
        throw new PersistenceException("This has not yet been implemented");
        if (getPropertyTypesAsCollection().contains(name)) {
            throw new PersistenceException("The name [" + name + "] already " +
                                           "exists in this ObjectType");
        }
    }
*/

    /**
     * Adds an optional one-way association (a role reference) to this Dynamic
     * ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @return the Property that was added to the ObjectType
     */
    public Property addOptionalAssociation(String name, ObjectType type) {
        return addAssociation(name, type, Property.NULLABLE, null);
    }

    /**
     * Adds an required one-way association (a role reference) to this Dynamic
     * ObjectType.  A default value must be specified.  The referenced type
     * <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @param defaultValue the default value of this Property
     * @return the Property that was added to the ObjectType
     */
    public Property addRequiredAssociation(String name,
                                           ObjectType type,
                                           Object defaultValue) {
        return addAssociation(name, type, Property.REQUIRED, defaultValue);
    }

    /**
     * Adds a multiplicitous one-way association (a role reference) to this
     * Dynamic ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @return the Property that was added to the ObjectType
     */
    public Property addCollectionAssociation(String name, ObjectType type) {
        return addAssociation(name, type, Property.COLLECTION, null);
    }

    /**
     * Adds a one-way association (a role reference) to this Dynamic
     * ObjectType.  The referenced type <b>must</b> support MDSQL.
     *
     * @param name the name of the role reference
     * @param type the type of the referenced type
     * @param mult the multiplicity of the type to add
     * @param defaultValue the default value of the new Property
     * @return the Property that was added to the ObjectType
     */
    public Property addAssociation(String name,
                                   ObjectType type,
                                   int mult,
                                   Object defaultValue) {
        // the name can only contains letters and numbers.  We throw
        // an exceptions if it contains anything else.
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (!(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
                  ('0' <= c && c <= '9'))) {
                throw new PersistenceException
                    ("The name of the DynamicObjectType must be alphanumeric. " +
                     "You tried to create it using [" + name + "]");
            }
        }

        boolean hasProperty = m_objectType.hasProperty(name);
        if (!hasProperty) {
            // let's check the properties not looking at case
            Iterator iter = m_objectType.getProperties();
            while (iter.hasNext() && !hasProperty) {
                hasProperty = name.toLowerCase().equals
                    (((Property)iter.next()).getName().toLowerCase());
            }
        }

        if (hasProperty) {
            throw new PersistenceException("The property [" + name + "] already " +
                                           "exists in this object type.");
        }

        Property property = new Property(name, type, mult);
        m_objectType.addProperty(property);

        if (!property.isCollection()) {
            // add a foreign key column here
            String columnName =
                m_generator.generateColumnName(m_objectType, name);

            // we need this both for the column type, and the FK constraint
            Column refkey = Utilities.getColumn(type);

            Column foreignKey =
                new Column(Utilities.getColumn(m_objectType).getTableName(),
                           columnName,
                           refkey.getType());

            JoinPath jp = new JoinPath();
            jp.addJoinElement(foreignKey, refkey);
            property.setJoinPath(jp);
        } else {
            // we need to create a mapping table here
            String tableName = 
                m_generator.generateMappingTableName(m_objectType, name);

            Column baseKey = Utilities.getColumn(m_objectType);
            Column foreignKey = Utilities.getColumn(type);

            String columnName1 =
                m_generator.generateColumnName(type, baseKey.getColumnName());
            String columnName2 =
                m_generator.generateColumnName(m_objectType,
                                               foreignKey.getColumnName());

            Column column1 = new Column(tableName,
                                        columnName1,
                                        baseKey.getType());

            Column column2 = new Column(tableName,
                                        columnName2,
                                        foreignKey.getType());

            JoinPath jp = new JoinPath();
            jp.addJoinElement(baseKey, column1);
            jp.addJoinElement(column2, foreignKey);

            property.setJoinPath(jp);

            m_mappingTables.add(property);
        }

        m_newProperties.add(property);

        if (defaultValue != null) {
            m_defaultValueMap.put(name, defaultValue);
        }

        return property;
    }

    /**
     *  This persists the changes made so that the information is stored
     *  and will not be lost on server restart.  No Events will work for
     *  new Attributes or RoleReferences before this is called because
     *  this generates the Events that need to be executed.
     *
     *  Specifically, this 
     *  <ol>
     *  <li>creates the DDL that must be executed to bring the
     *      database in sync with the events and prepare the
     *      object events to be generated</li>
     *  <li>creates or updates the needed events in memory</li>
     *  <li>generate the new PDL file to represent this object type</li>
     *  <li>execute the DDL</li>
     *  <li>if the DDL executes successfully, update the database to
     *      reflect the new syntax.  Otherwise, throw a PersistenceException
     *      </li>
     *  </ul>
     */
    public ObjectType save() {
        // 1. Create the DDL that needs to be executed and prepare for 
        //    the object events
        Collection ddlToAdd = m_generator.generateTable(m_objectType,
                                                        m_keyColumn,
                                                        m_newProperties,
                                                        m_defaultValueMap);

        // 2. Generate the PDL.
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        m_objectType.outputPDL(new PrintStream(stream), false);
        String pdl = "model " + m_objectType.getModel().getName() + ";" +
            Utilities.LINE_BREAK + stream.toString();

        // 3. Create the events and schema in memory
        MDSQLGenerator generator = MDSQLGeneratorFactory.getInstance();

        for (int i = 0; i < CompoundType.NUM_EVENT_TYPES; i++) {
            Event event = generator.generateEvent(m_objectType, i);
        }

        Iterator props = m_objectType.getDeclaredProperties();

        while (props.hasNext()) {
            Property prop = (Property)props.next();

            if (!prop.isAttribute()) {
                for (int i = 0; i < Property.NUM_EVENT_TYPES; i++) {
                    Event event = generator.generateEvent(m_objectType,
                                                          prop,
                                                          i);
                }
            }
        }

        // 4. execute the DDL
        try {
            java.sql.Statement statement = SessionManager.getSession()
                                                         .getConnection()
                                                         .createStatement();

            if (ddlToAdd != null) {
                Iterator iterator = ddlToAdd.iterator();
                while (iterator.hasNext()) {
                    String ddl = (String)iterator.next();
                    statement.executeUpdate(ddl);
                }
            }
            
            Iterator it = m_mappingTables.iterator();

            while (it.hasNext()) {
                Property prop = (Property)it.next();

                statement.executeUpdate(
                    m_generator.generateMappingTable(m_objectType, prop));
            }

        } catch (SQLException e) {
            StringBuffer ddl = new StringBuffer();
            Iterator iterator = ddlToAdd.iterator();
            while (iterator.hasNext()) {
                ddl.append((String)iterator.next() + 
                           com.arsdigita.persistence.Utilities.LINE_BREAK);
            }

            throw PersistenceException.newInstance(e.getMessage() + 
                                           Utilities.LINE_BREAK +
                                           "SQL for ADD: " + ddl.toString(), e);
        }
            

        // 5. if DDL executing is successful, update the database to
        //    reflect the new syntax
        try {
            if (m_dataObject == null) {
                m_dataObject = SessionManager.getSession().create(objectTypeString);
                m_dataObject.set("id", Sequences.getNextValue());
                m_dataObject.set("objectType", objectTypeString);
                m_dataObject.set("dynamicType", m_objectType.getQualifiedName());
                m_dataObject.set("displayName", objectTypeString);
            }
                   
            m_dataObject.set("pdlFile", pdl);
            m_dataObject.save();
        } catch (SQLException e) {
            throw PersistenceException.newInstance("Error saving PDL file", e);
        }

        // 6. Iterate over the child objects, generating a list of objecttypes
        //    that are descendents of this object type so that they, and any
        //    associations of their type, can be regenerated.
        Collection objectTypes = MetadataRoot.getMetadataRoot()
                                             .getObjectTypes();
        Collection affected = new ArrayList();
        Iterator it;
        boolean oneMore;

        affected.add(m_objectType);

        do {
            it = objectTypes.iterator();
            oneMore = false;

            while (it.hasNext()) {
                ObjectType type = (ObjectType)it.next();

                if (type.getSupertype() != null) {
                    Iterator parents = affected.iterator();

                    while (parents.hasNext()) {
                        ObjectType parent = (ObjectType)parents.next();
                        if (type.getSupertype().equals(parent)) {
                            affected.add(type);
                            it.remove();
                            oneMore = true;

                            break;
                        }
                    }
                }
            }
        } while (oneMore);

        // so now we have a list of affected object types, now regenerate them

        it = affected.iterator();

        while (it.hasNext()) {
            ObjectType type = (ObjectType)it.next();

            for (int i = 0; i < CompoundType.NUM_EVENT_TYPES; i++) {
                Event event = generator.generateEvent(type, i);
            }

            props = type.getDeclaredProperties();

            while (props.hasNext()) {
                Property prop = (Property)props.next();

                if (!prop.isAttribute()) {
                    for (int i = 0; i < Property.NUM_EVENT_TYPES; i++) {
                        Event event = generator.generateEvent(type, prop, i);
                    }
                }
            }
        }

        // now loop through all the properties, regenerating any that reference
        // anything in "affected".
        it = objectTypes.iterator();

        while (it.hasNext()) {
            ObjectType type = (ObjectType)it.next();

            props = type.getDeclaredProperties();

            while (props.hasNext()) {
                Property prop = (Property)props.next();

                if (!prop.isAttribute() && affected.contains(prop.getType())) {
                    for (int i = 0; i < Property.NUM_EVENT_TYPES; i++) {
                        Event event = generator.generateEvent(type, prop, i);
                    }
                }
            }
        }

        m_isNew = false;
        m_ddlToAdd = null;
        m_newProperties = new ArrayList();
        m_mappingTables = new ArrayList();
        m_keyColumn = null;
        m_defaultValueMap = new HashMap();

        return m_objectType;
    }


    /**
     *  This method returns the ObjectType that is being manipulated
     *  by this DynamicObjectType
     */
    public ObjectType getObjectType() {
        return m_objectType;
    }

    /**
     *  This prints out a String representation of this object type
     */
    public String toString() {
        String appendString = "";
        if (m_ddlToAdd.length() > 0) {
            appendString = Utilities.LINE_BREAK + "The following will be " + 
                "added to the table:" + m_ddlToAdd.toString();
        }

        return m_objectType.toString() + appendString;
    }


    /**
     *  This allows the user to either input or output a given
     *  data object type x
     *  <p>
     *  This is only meant to be called from the command line.
     *  the usage is 
     *  <code>
     *  java com.arsdigita.persistence.metadataDyanmicObjectType 
     *  &lt;[import | export]&gt; &lt;DynamicObjectType&gt; &lt;FileLocation&gt; 
     *  &lt;StartupScript&gt; &lt;WebAppRoot&gt;
     *  </code>
     *  <p>
     *  <ul>
     *  <li>The first item, "import" or "export" tells the method whether
     *  you are loading a file into the database or you want to print
     *  a file in the database into the file system.</li>
     *
     *  <li>The DynamicObjectType is the fully qualified name of the
     *      object type.  An example is 
     *      <code>com.arsdigita.cms.MyDynamicType</code>
     *  </li>
     *
     *  <li>The FileLocation is location in the file system where 
     *      the file should be read from or written to.  For example
     *      /home/tomcat/webapps/enterprise/dynamictypes/cms/MyDynamicType.pdl
     *  </li>
     *
     *  <li>The StartupScript is the location of your enterprise.init script
     *      (or the file that is used to specify the initializers to run
     *       as well as how to access the database).  For example
     *       <code>
     *       /home/tomcat/webapps/acs/WEB-INF/resources/enterprise.init
     *       </code>
     *  </li>
     *
     *  <li>The WebAppRoot is used by the initializers to find the correct
     *      code to execute.  For example <code>/home/tomcat/webapps/enterprise
     *      </code>
     *  </li>
     *  </ul>
     *
     *  So, to export the dynamic type MyDynamicType from the database to
     *  the file system, you can type
     *  <code>
     *  java com.arsdigita.persistence.metadata.DynamicObjectType export 
     *  com.arsdigita.cms.MyDynamicType /tmp/MyDynamicType.pdl 
     *  /home/tomcat/webapps/enterprise/WEB-INF/resources/enterprise.init 
     *  /home/tomcat/webapps/enterprise
     *  </code>
     *  <p>
     *  Make sure that you have the DynamicObjectType.class file
     *  in your classpath (which is something that is not in your classpath
     *  when you typically start your server)
     */
    static public void main(String args[]) {
        String IMPORT = "import";
        String EXPORT = "export";
        String usageString = "Usage: java DyanmicObjectType " +
            " <[" + IMPORT + " | " + EXPORT + "]> <DynamicObjectType> " +
            "<FileLocation> <StartupScript> <WebAppRoot>";

        if (args.length != 5) {
            System.err.println(usageString);
            System.exit(1);
        }

        String type = args[0];
        if (!(type.equalsIgnoreCase(IMPORT) || type.equalsIgnoreCase(EXPORT))) {
            System.err.println
                ("The first argument must specify whether you wish to " +
                 "'import' or 'export'" + Utilities.LINE_BREAK + usageString);
        }

        String objectType = args[1];
        String fileName = args[2];
        String startupScript = args[3];
        String webAppRoot = args[4];

        Startup startup = new Startup(webAppRoot, startupScript);
        startup.setLastInitializer("com.arsdigita.persistence.Initializer");
        startup.init();

        TransactionContext txn = SessionManager.getSession()
                                               .getTransactionContext();
        // open the transaction
        if (! txn.inTxn()) {
            txn.beginTxn();
        }

        // get the data object we will be working with
        DataObject dataObject;
        DataCollection collection = SessionManager.getSession()
                                                  .retrieve(objectTypeString);
        collection.addEqualsFilter("dynamicType", objectType);
        if (collection.next()) {
            dataObject = collection.getDataObject();
        } else {
            dataObject = SessionManager.getSession().create(objectTypeString);
            try {
                dataObject.set("id", Sequences.getNextValue());
            } catch (SQLException e) {
                System.err.println("Unable to create sequence:" + 
                                   Utilities.LINE_BREAK + e.getMessage());
                txn.commitTxn();
                System.exit(1);
            }
            dataObject.set("objectType", objectTypeString);
            dataObject.set("dynamicType", objectType);
            dataObject.set("displayName", objectType);
        }

        if (type.equalsIgnoreCase(IMPORT)) {
            // read in the file
            try {
                BufferedReader reader = new BufferedReader
                    (new FileReader(fileName));
                StringBuffer pdlFile = new StringBuffer();

                try {
                    String nextLine = reader.readLine();
                    while (nextLine != null) {
                        pdlFile.append(nextLine + Utilities.LINE_BREAK);
                        nextLine = reader.readLine();
                    }
                } catch (IOException e) {
                    String suffix = "";
                    if (!"".equals(pdlFile.toString())) {
                        suffix = "We were able read the following" + 
                            pdlFile.toString();
                    }
                    System.err.println("There was an error reading the file [" +
                                       fileName + "].  " + suffix);
                }

                dataObject.set("pdlFile", pdlFile.toString());
                dataObject.save();
            } catch (FileNotFoundException e) {
                System.err.println("The file you have provided to input [" +
                                   fileName + "] cannot be accessed.");
                if (!(new File(fileName)).canRead()) {
                    System.err.println("The system cannot read the file");
                }
                txn.commitTxn();
                System.exit(1);
            }
            
        } else {
            String pdlFile = (String)dataObject.get("pdlFile");
            if (pdlFile == null) {
                System.err.println("The object type you have requested [" +
                                   objectType + "] cannot be found.  Please " +
                                   "check the type and try again.");
                txn.commitTxn();
                System.exit(1);                
            }

            // write the PDL to the file system
            try {
                (new PrintWriter(new FileOutputStream(fileName))).print(pdlFile);
            } catch (FileNotFoundException e) {
                System.err.println("The file you have provided to input [" +
                                   fileName + "] cannot be accessed.");
                if (!(new File(fileName)).canWrite()) {
                    System.err.println("The system cannot write to the file");
                }
            }
        }

        txn.commitTxn();
        startup.destroy();
    }
}


