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

import com.arsdigita.persistence.PersistenceTestCase;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.math.BigDecimal;
import org.apache.log4j.Category;

/**
 * DynamicObjectTypeTest tests to make sure that the DynamicObjectType
 * class works as advertised.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class DynamicObjectTypeTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/DynamicObjectTypeTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";
    private static Category s_log = 
        Category.getInstance(DynamicObjectTypeTest.class.getName());

    private MetadataRoot m_root;
    private ArrayList m_tables = new ArrayList();
    private ArrayList m_objectTypes = new ArrayList();

    String superTypeString = "com.arsdigita.kernel.ACSObject";
    String objectTypeModel = "com.arsdigita.kernel";
    ObjectType supertype;
    DynamicObjectType dot;
    int counter = 0;
    String objectTypeName = "newObject00";

    public DynamicObjectTypeTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        super.persistenceSetUp();

        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        m_root = MetadataRoot.getMetadataRoot();

        supertype = m_root.getObjectType(superTypeString);
        while (m_root.getObjectType(objectTypeModel + "." +  
                                    objectTypeName) != null) {
            counter++;
            String nextCount = (new Integer(counter)).toString();
            if (counter < 10) {
                nextCount = "0" + nextCount;
            }
            objectTypeName = objectTypeName.substring
                (0, objectTypeName.length() - 2) + nextCount;
        }

        dot = new DynamicObjectType(objectTypeName, supertype);
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        Iterator iter = m_tables.iterator();
        java.sql.Statement statement = null;
        try {
            statement = SessionManager.getSession()
                .getConnection()
                .createStatement();
            while (iter.hasNext()) {
                String table = (String)iter.next();
                try {
                    statement.executeUpdate("drop table " + table);
                } catch (Exception e) {
                    s_log.info("Error executing statement " +
                                       "'drop table " + table + "': " + e);
                }        
            }
        } catch (Exception e) {
            s_log.info("Error creating statement: " + e.getMessage());
        } finally {
            try {
                statement.close();
            } catch (Exception e) { 
                //ignore
            }
        }

        getSession().getTransactionContext().abortTxn();
        getSession().getTransactionContext().beginTxn();

        iter = m_objectTypes.iterator();
        while (iter.hasNext()) {
            DataOperation operation = SessionManager.getSession()
                                                    .retrieveDataOperation
                ("examples.DataOperationToDeleteTestDynamicObjectTypes");
            String name = ((ObjectType)iter.next()).getQualifiedName();
            operation.setParameter("dynamicType", name.toLowerCase());
            operation.execute();
        }
        // this is here so that the "delete" operation above takes 
        getSession().getTransactionContext().commitTxn();
        getSession().getTransactionContext().beginTxn();
        super.persistenceTearDown();
    }


    public void testConstructors() throws Exception {
        dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
        dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
        dot.addRequiredAttribute("requiredAttribute", MetadataRoot.BOOLEAN, 
                                 new Boolean(true));
        dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
                                 "my new text string");
        ObjectType objectType = dot.save();

        String tableName = objectType.getReferenceKey().getTableName();
        m_tables.add(tableName);
        m_objectTypes.add(objectType);

        // make sure that the supertype is valid
        String actualSuperType = objectType.getSupertype().getQualifiedName();

        assert("The supertype should have been '" + superTypeString + "'" +
               " but actually was '" + actualSuperType + "'",
               superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(), 
                           new String[] {"optionalAttribute", 
                                         "optionalAttribute2", 
                                         "requiredAttribute", 
                                         "requiredAttribute2"});
        
        dot = new DynamicObjectType("com.arsdigita.kernel." + objectTypeName);
        dot.addRequiredAttribute("requiredAttribute3", MetadataRoot.BOOLEAN,
                                 new Boolean(false));
        dot.addRequiredAttribute("requiredAttribute4", MetadataRoot.STRING, 400,
                                 "my really cool default");
        objectType = dot.save();

        actualSuperType = objectType.getSupertype().getQualifiedName();

        assert("The supertype should have been '" + superTypeString + "'" +
               " but actually was '" + actualSuperType + "'",
               superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(), 
                           new String[] {"optionalAttribute", 
                                         "optionalAttribute2", 
                                         "requiredAttribute", 
                                         "requiredAttribute2", 
                                         "requiredAttribute3", 
                                         "requiredAttribute4"});

        dot = new DynamicObjectType(objectType);
        dot.addRequiredAttribute("requiredAttribute5", MetadataRoot.DOUBLE,
                                 new Double(4));
        dot.addRequiredAttribute("requiredAttribute6", MetadataRoot.DATE, 400,
                                 new Date());
        objectType = dot.save();

        actualSuperType = objectType.getSupertype().getQualifiedName();

        assert("The supertype should have been '" + superTypeString + "'" +
               " but actually was '" + actualSuperType + "'",
               superTypeString.equals(actualSuperType));

        validateProperties(objectType.getDeclaredProperties(), 
                           new String[] {"optionalAttribute", 
                                         "optionalAttribute2", 
                                         "requiredAttribute", 
                                         "requiredAttribute2", 
                                         "requiredAttribute3", 
                                         "requiredAttribute4", 
                                         "requiredAttribute5", 
                                         "requiredAttribute6"});
        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation
            ("examples.DataOperationToDeleteTestDynamicObjectTypes");
        operation.setParameter("dynamicType", "com.arsdigita.kernel." + 
                               objectTypeName);


        // now test 
        //DynamicObjectType(String name, ObjectType supertype, String model) 
        dot = new DynamicObjectType("testType", supertype, null);
        ObjectType dotObject = dot.save();
        Model dotModel = dotObject.getModel();
        m_objectTypes.add(dotObject);
        m_tables.add(dotObject.getReferenceKey().getTableName());

        dot = new DynamicObjectType("testType2", null, dotObject.getModel());
        Property prop = dot.addRequiredAttribute("requiredAttribute6", 
                                                 MetadataRoot.DATE, 400,
                                                 new Date());
        dotObject = dot.save();

        m_objectTypes.add(dotObject);
        m_tables.add(prop.getColumn().getTableName());

        assert("The model name should be [com.arsdigita.kernel] not [" +
               dotObject.getModel().getName() + "]", 
               "com.arsdigita.kernel".equals(dotObject.getModel().getName()));
        assert(dotObject.getModel().equals(dotModel));

        // make sure an invalid name throws an exception
        try {
            dot = new DynamicObjectType("my.test.object.type", supertype);
            fail("using an invalid type should have thrown an exception");
        } catch (PersistenceException e) {
            // it should be here so we fall through
        }

        // make sure it is not possible to create an object type that
        // is supposed to be a static object type
        try {
            dot = new DynamicObjectType("com.arsdigita.kernel.ACSObject");
            fail("Static object types should not be allowed to become " +
                 "dynamic object types");
        } catch (PersistenceException e) {
            // it should be here so we fall through
        }
    }


    /**
     *  This tests the addOptionalAttribute method within DynamicObjectType
     */
    public void testAddRequiredAttribute() {
        Property prop = dot.addRequiredAttribute("myOptionalProp", m_root.DATE,
                                                 new Date());
 
        validateProperties(dot.getObjectType().getDeclaredProperties(), 
                           new String[] {"myOptionalProp"});

        Property prop2 = dot.addRequiredAttribute("myOptionalProp2", 
                                                  m_root.FLOAT, 400,
                                                  new Float(4));
        assert("the size for the new string attribute should be 400, not " + 
               prop2.getColumn().getSize(), prop2.getColumn().getSize() == 400);
                                             
        validateProperties(dot.getObjectType().getDeclaredProperties(), 
                           new String[] {"myOptionalProp", "myOptionalProp2"});


       // make sure that the column names are different but the table names
        // are the same
        assert("the tables names are different when they should both be " +
               "the same", prop.getColumn().getTableName().equals
               (prop2.getColumn().getTableName()));

        assert("the column names are the same when they should be different ", 
               !prop.getColumn().getColumnName().equals
               (prop2.getColumn().getColumnName()));

        // duplicate names should throw an exception
        try {
            prop = dot.addRequiredAttribute("myOptionalProp", m_root.INTEGER,
                                            new Integer(3));
            fail("Adding attributes with the same name should throw an " +
                 "exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        // same name, different caps.  We do not support this one either
        try {
            prop = dot.addRequiredAttribute("myoptionalprop", m_root.FLOAT,
                                            new Float(444));
            fail("Adding attributes with the same name (but different " + 
                 "capitalization should throw an exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        assert("An optional attribute should be required",
               prop.getMultiplicity() == Property.REQUIRED);
    }


    /**
     *  This tests the addOptionalAttribute method within DynamicObjectType
     */
    public void testAddOptionalAttribute() {
        Property prop = dot.addOptionalAttribute("myOptionalProp", m_root.INTEGER);
 
        validateProperties(dot.getObjectType().getDeclaredProperties(), 
                           new String[] {"myOptionalProp"});

        Property prop2 = dot.addOptionalAttribute("myOptionalProp2", 
                                                  m_root.STRING, 400);
        assert("the size for the new string attribute should be 400, not " + 
               prop2.getColumn().getSize(), prop2.getColumn().getSize() == 400);
                                             
        validateProperties(dot.getObjectType().getDeclaredProperties(), 
                           new String[] {"myOptionalProp", "myOptionalProp2"});

        // make sure that the column names are different but the table names
        // are the same
        assert("the tables names are different when they should both be " +
               "the same", prop.getColumn().getTableName().equals
               (prop2.getColumn().getTableName()));

        assert("the column names are the same when they should be different ", 
               !prop.getColumn().getColumnName().equals
               (prop2.getColumn().getColumnName()));

        // duplicate names should throw an exception
        try {
            prop = dot.addOptionalAttribute("myOptionalProp", m_root.INTEGER);
            fail("Adding attributes with the same name should throw an " +
                 "exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        // same name, different caps.  We do not support this one either
        try {
            prop = dot.addOptionalAttribute("myoptionalprop", m_root.INTEGER);
            fail("Adding attributes with the same name (but different " + 
                 "capitalization should throw an exception");
        } catch (PersistenceException e) {
            // this should be here so let it fall through
        }

        assert("An optional attribute should be nullable",
               prop.getMultiplicity() == Property.NULLABLE);
    }

    public void testAddRoleReference() {
        ObjectType acsobj = 
            MetadataRoot.getMetadataRoot()
                        .getObjectType("com.arsdigita.kernel.ACSObject");

        DataObject defaultObj =
            getSession().create("com.arsdigita.kernel.ACSObject");
        defaultObj.set("id", new BigDecimal(-50));
        defaultObj.set("objectType", "com.arsdigita.kernel.ACSObject");
        defaultObj.set("displayName", "Default Object");
        defaultObj.save();

        dot.addCollectionAssociation("myCollectionRR", acsobj);
        dot.addOptionalAssociation("myOptionalRR", acsobj);
        dot.addRequiredAssociation("myRequiredRR", acsobj, new BigDecimal(-50));

        validateProperties(dot.getObjectType().getDeclaredProperties(),
                           new String[] {"myCollectionRR",
                                         "myOptionalRR",
                                         "myRequiredRR"});

        ObjectType objectType = dot.save();

        String mappingTable =
            ((JoinElement)objectType.getProperty("myCollectionRR")
                                    .getJoinPath()
                                    .getPath()
                                    .get(0)).getTo().getTableName();

        m_tables.add(mappingTable);

        String tableName = objectType.getReferenceKey().getTableName();
        m_tables.add(tableName);
        m_objectTypes.add(objectType);

        DataObject associated =
            getSession().create("com.arsdigita.kernel.ACSObject");
        associated.set("id", new BigDecimal(-51));
        associated.set("objectType", "com.arsdigita.kernel.ACSObject");
        associated.set("displayName", "Default Object");
        associated.save();

        DataObject testObj = getSession().create(objectType);
        testObj.set("id", new BigDecimal(-52));
        testObj.set("objectType", objectType.getQualifiedName());
        testObj.set("displayName", "Default Object");
        testObj.set("myOptionalRR", associated);

        // TODO: remove the line below once 192076 is complete
        testObj.set("myRequiredRR", defaultObj);

        DataAssociation assoc = (DataAssociation)testObj.get("myCollectionRR");
        assoc.add(defaultObj);
        assoc.add(associated);

        testObj.save();

        DataObject associated2 = (DataObject)testObj.get("myOptionalRR");
        DataObject associated3 = (DataObject)testObj.get("myRequiredRR");

        assert("Optional attribute differs", associated.equals(associated2));
        assert("Required attribute differs", defaultObj.equals(associated3));

        assoc = (DataAssociation)testObj.get("myCollectionRR");
        DataAssociationCursor cursor = assoc.cursor();

        while (cursor.next()) {
            cursor.remove();
        }

        testObj.delete();
        defaultObj.delete();
        associated.delete();
    }

    public void testResaving() {
        ObjectType objectType = dot.save();

        String tableName = objectType.getReferenceKey().getTableName();
        m_tables.add(tableName);
        m_objectTypes.add(objectType);
 
        dot.addOptionalAttribute("testAttribute", MetadataRoot.STRING);
        ObjectType type = dot.save();

        dot = new DynamicObjectType(type);

        dot.addRequiredAttribute("testRequired", MetadataRoot.STRING, "foo");
        type = dot.save();

        DataObject testObj = getSession().create(type);
        testObj.set("id", new BigDecimal(-60));
        testObj.set("objectType", type.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttribute", "Test Attribute");
        testObj.set("testRequired", "Test Required");
        testObj.save();
        OID testOID = testObj.getOID();

        testObj = getSession().retrieve(testOID);

        String value = (String)testObj.get("testAttribute");
        assert("Optional value not saved", value.equals("Test Attribute"));

        value = (String)testObj.get("testRequired");
        assert("Required value not saved", value.equals("Test Required"));

        testObj.delete();
    }

    public void testInvalidName() {
        try {
            dot.addOptionalAttribute("foo bar", MetadataRoot.STRING);

            fail("No exception thrown on invalid attribute name.");
        } catch (PersistenceException e) {
        }

        try {
            dot.addOptionalAssociation("foo bar", supertype);

            fail("No exception thrown on invalid association name.");
        } catch (PersistenceException e) {
        }
    }

    // test that all child objects have their events regenerated when a 
    // parent class is altered
    public void testAlteredParents() {
        ObjectType type = dot.save();

        DynamicObjectType sub = new DynamicObjectType("childType", type);
        ObjectType type2 = sub.save();

        DynamicObjectType subsub = new DynamicObjectType("subsubType", type2);
        ObjectType type3 = subsub.save();

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        dot.save();

        // The below code will throw an error if the event regeneration fails
        DataObject testObj = getSession().create(type2);
        testObj.set("id", new BigDecimal(-53));
        testObj.set("objectType", type2.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttr1", "Test Attribute");
        testObj.save();

        testObj = getSession().retrieve(new OID(type2, new BigDecimal(-53)));

        String value = (String)testObj.get("testAttr1");
        assert("Child Value not saved correctly",
               value.equals("Test Attribute"));

        testObj.delete();

        testObj = getSession().create(type3);
        testObj.set("id", new BigDecimal(-54));
        testObj.set("objectType", type3.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAttr1", "Test Attribute");
        testObj.save();

        testObj = getSession().retrieve(new OID(type3, new BigDecimal(-54)));

        value = (String)testObj.get("testAttr1");
        assert("Grand child value not saved correctly",
                value.equals("Test Attribute"));

        testObj.delete();

        m_tables.add(type3.getReferenceKey().getTableName());
        m_objectTypes.add(type3);

        m_tables.add(type2.getReferenceKey().getTableName());
        m_objectTypes.add(type2);

        m_tables.add(type.getReferenceKey().getTableName());
        m_objectTypes.add(type);
    }

    // test the case where a role reference type is changed
    public void testAlteredAssociation() {
        ObjectType type = dot.save();

        DynamicObjectType subdot = new DynamicObjectType("subtype", type);
        ObjectType subtype = subdot.save();

        DynamicObjectType dot2 =
            new DynamicObjectType("associater", supertype);
        dot2.addOptionalAssociation("testAssoc1", type);
        dot2.addOptionalAssociation("testAssoc2", subtype);

        ObjectType type2 = dot2.save();

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        dot.save();

        DataObject associated = getSession().create(type);
        associated.set("id", new BigDecimal(-56));
        associated.set("objectType", type.getQualifiedName());
        associated.set("displayName", "Default Object");
        associated.set("testAttr1", "Test Attr");
        associated.save();
        
        DataObject subassociated = getSession().create(subtype);
        subassociated.set("id", new BigDecimal(-58));
        subassociated.set("objectType", subtype.getQualifiedName());
        subassociated.set("displayName", "Default Object");
        subassociated.set("testAttr1", "Test Attr");
        subassociated.save();
        
        DataObject testObj = getSession().create(type2);
        testObj.set("id", new BigDecimal(-57));
        testObj.set("objectType", type2.getQualifiedName());
        testObj.set("displayName", "Test Object");
        testObj.set("testAssoc1", associated);
        testObj.set("testAssoc2", subassociated);
        testObj.save();

        testObj = getSession().retrieve(new OID(type2, new BigDecimal(-57)));

        DataObject associated2 = (DataObject)testObj.get("testAssoc1");
        String value = (String)associated2.get("testAttr1");

        assert("Associated value not retrieved correctly",
               value.equals("Test Attr"));

        associated2 = (DataObject)testObj.get("testAssoc2");
        value = (String)associated2.get("testAttr1");

        assert("Associated subtype value not retrieved correctly",
               value.equals("Test Attr"));

        testObj.delete();
        associated.delete();
        subassociated.delete();

        m_tables.add(type2.getReferenceKey().getTableName());
        m_objectTypes.add(type2);

        m_tables.add(subtype.getReferenceKey().getTableName());
        m_objectTypes.add(subtype);

        m_tables.add(type.getReferenceKey().getTableName());
        m_objectTypes.add(type);
    }

    public void testSave() {
        // TODO
        // 1. test for and handle the situation where the generated table
        //    name already exists
        // 2. make the checks with locking the table, etc for race
        //    conditions
        // 3. Test for the situation where one of the column names
        //    is a duplicate (even though it should not happen)

    }


    /**
     *  This takes an Iterator and an Array and makes sure
     *  that they contain the same items.  It fails if they do not
     */
    private void validateProperties(Iterator properties, String[] propNames) {
        ArrayList list = new ArrayList();
        ArrayList list2 = new ArrayList();
        for (int i=0; i<propNames.length; i++) {
            list.add(propNames[i]);
        }

        int count = 0;
        while (properties.hasNext()) {
            count++;
            String name = ((Property)properties.next()).getName();
            list2.add(name);
            assert("The ObjectType contained the property [" + name + "] " + 
                   "but it should not have", list.contains(name));
        }
        
        if (count != list.size()) {
            fail("The ObjectType had " + count + " elements but the array " +
                 "only had " + list.size() + Utilities.LINE_BREAK + 
                 "The ObjectType had " + list2.toString() + 
                 Utilities.LINE_BREAK + "while it should have had " +
                 Utilities.LINE_BREAK + list.toString());
        }
    }
}
