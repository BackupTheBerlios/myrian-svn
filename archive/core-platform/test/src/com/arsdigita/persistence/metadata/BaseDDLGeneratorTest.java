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

import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.PersistenceTestCase;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.db.Sequences;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Date;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * <p> This class performs unit tests on
 *  com.arsdigita.persistence.metadata.BaseDDLGenerator </p>
 *
 * @author <a href="mailto:jorris@arsdigita.com">jorris@arsdigita.com</a>
 * @version $Revision: #5 $ $Date: 2002/08/30 $
 *
 * @see com.arsdigita.persistence.metadatax.ObjectType
 */


public class BaseDDLGeneratorTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/BaseDDLGeneratorTest.java#5 $ by $Author: dennis $, $DateTime: 2002/08/30 17:07:43 $";

    private static Logger s_log =
        Logger.getLogger(BaseDDLGeneratorTest.class.getName());

    public BaseDDLGeneratorTest(String name) {
        super(name);
    }

    Session m_session;
    DDLGenerator m_generator;
    private ArrayList m_tables = new ArrayList();
    private ArrayList m_objectTypes = new ArrayList();
    private MetadataRoot m_root = MetadataRoot.getMetadataRoot();

    protected void setUp() {
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        m_session = SessionManager.getSession();
        // for some reason, TearDown is not always being called
        // at the end of the tests so we are doing it here just
        // in case.
        if (m_session.getTransactionContext().inTxn()) {
            m_session.getTransactionContext().abortTxn();
        }
        m_session.getTransactionContext().beginTxn();
        m_generator = DDLGeneratorFactory.getInstance();
    }

    protected void tearDown() {
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
            } catch (SQLException e) {
                s_log.info("Error closing statement: " + e.getMessage());
            }
        }

        if (getSession().getTransactionContext().inTxn()) {
            getSession().getTransactionContext().abortTxn();
        }

        m_session.getTransactionContext().beginTxn();
        iter = m_objectTypes.iterator();
        while (iter.hasNext()) {
            DataOperation operation = SessionManager.getSession()
                .retrieveDataOperation
                ("examples.DataOperationToDeleteTestDynamicObjectTypes");
            String name = ((ObjectType)iter.next()).getQualifiedName();
            operation.setParameter("dynamicType", name.toLowerCase());
            operation.execute();
        }
        getSession().getTransactionContext().commitTxn();
        getSession().getTransactionContext().beginTxn();
    }


    /**
     *  Here we want to make sure that we get unique names that
     *  are less than 26 characters long
     */
    public void testGenerateTableName() {
        //String generateTableName(String modelName, String objectName);
        Collection list = new ArrayList();

        for (int i = 0; i < 200; i++) {
            String tableName = m_generator.generateTableName("com.arsdigita.foo",
                                                             "myObjectName");
            assertTrue("generateTable produced duplicated table names: " + tableName,
                   !list.contains(tableName.toLowerCase()));
            list.add(tableName.toLowerCase());
        }

        // now that we know that it does not put out duplicates, let's check
        // for length
        String tableName = m_generator.generateTableName
            ("my.long.project.namespace.foo", "myReallyReallyLongObjectName");
        assertTrue("the table name passed back is too long",
               tableName.length() <= 26);

        // using a table we know that is in the system, let's see if
        // we get back a unique name
        tableName = m_generator.generateTableName("acs", "objects");
        assertTrue("the table generated is not correct",
               !"acs_objects".equals(tableName));

    }



    /**
     *  This tests the code to create a table and to drop
     *  a table by actually creating and dropping a table.
     *  It is definately not robust but it is a start
     */
    /*
      public void testGenerateCreateAndDropTableFAILS() {
      // create object type
      String superTypeString = "com.arsdigita.kernel.ACSObject";
      String objectTypeModel = "com.arsdigita.kernel";
      DynamicObjectType dot = new DynamicObjectType("myTestObjectType",
      m_root.getObjectType
      ("com.arsdigita.kernel.ACSObject"));
      dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
      dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
      dot.addRequiredAttribute("requiredAttribute", MetadataRoot.BOOLEAN,
      new Boolean(true));
      dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
      "my required attribute");
      ObjectType objectType = dot.save();
      m_tables.add(objectType.getReferenceKey().getTableName());
      m_objectTypes.add(objectType);

      String createTableString = m_generator.generateCreateTable(objectType);
      String dropTableString = m_generator.generateDropTable(objectType);

      assertTrue("The create table should start with 'create table'.  Instead, " +
      "it was " + Utilities.LINE_BREAK + createTableString,
      createTableString.indexOf("create table") < 2 ||
      createTableString.indexOf("create table") > 0);

      assertTrue("The drop table string is not correct.  It should have been " +
      Utilities.LINE_BREAK + "drop table " +
      objectType.getReferenceKey().getTableName() +
      Utilities.LINE_BREAK + "but actuall was " +
      Utilities.LINE_BREAK + dropTableString,
      ("drop table " + objectType.getReferenceKey().getTableName())
      .equals(dropTableString));

      try {
      java.sql.Statement statement = SessionManager.getSession()
      .getConnection()
      .createStatement();
      statement.executeUpdate(createTableString);
      statement.executeUpdate(dropTableString);
      } catch (SQLException e) {
      fail("The system either failed to create or failed to drop the " +
      "table" + Utilities.LINE_BREAK + " CREATE: " +
      Utilities.LINE_BREAK + createTableString +
      Utilities.LINE_BREAK + " DROP: " + Utilities.LINE_BREAK +
      dropTableString + Utilities.LINE_BREAK + e.getMessage());
      }
      }
    */


    /**
     *  This makes sure that the system generates the correct syntax
     *  for adding columns to an existing table
     */
    /*
      public void testAlterTablePrefixAddColumns() {
      String prefix = m_generator.alterTablePrefixAddColumns("myTable");
      int alterIndex = prefix.indexOf("alter");
      int tableIndex = prefix.indexOf("table");
      int tableNameIndex = prefix.indexOf("myTable");
      int addIndex = prefix.indexOf("add");
      int parenIndex = prefix.indexOf("(");
      assertTrue("One of the required items was not included or was in the " +
      "wrong order.  The system returned [" + prefix + "] but should " +
      "have returned something like [alter table myTable add (]",
      alterIndex > -1 && alterIndex < tableIndex &&
      tableIndex < tableNameIndex && tableNameIndex < addIndex &&
      addIndex < parenIndex);
      }
    */

    /**
     *  This makes sure that the system generates the correct syntax
     *  for removing columns from existing tables
     */
    /*
      public void testAlterTablePrefixRemoveColumns() {
      String prefix = m_generator.alterTablePrefixRemoveColumns("myTable");
      int alterIndex = prefix.indexOf("alter");
      int tableIndex = prefix.indexOf("table");
      int tableNameIndex = prefix.indexOf("myTable");
      int dropIndex = prefix.indexOf("drop");
      int parenIndex = prefix.indexOf("(");
      assertTrue("One of the required items was not included or was in the " +
      "wrong order.  The system returned [" + prefix + "] but should " +
      "have returned something like [alter table myTable drop (]",
      alterIndex > -1 && alterIndex < tableIndex &&
      tableIndex < tableNameIndex && tableNameIndex < dropIndex &&
      dropIndex < parenIndex);
      }
    */

    /**
     *  This makes sure that the system generates the correct syntax
     *  for creating a new table given a table name
     */
    /*
      public void testCreateTablePrefix() {
      String prefix = m_generator.createTablePrefix("myTable");
      int createIndex = prefix.indexOf("create");
      int tableIndex = prefix.indexOf("table");
      int tableNameIndex = prefix.indexOf("myTable");
      int parenIndex = prefix.indexOf("(");
      assertTrue("One of the required items was not included or was in the " +
      "wrong order.  The system returned [" + prefix + "] but should " +
      "have returned something like [create table myTable (]",
      createIndex > -1 && createIndex < tableIndex &&
      tableIndex < tableNameIndex && tableNameIndex < parenIndex);
      }
    */

    /**
     *  This makes sure that the system generates unique column names
     *  that can be added to an alter table or create table statement
     */
    public void testGenerateColumnName() {
        //TODO
        //String generateColumnName(ObjectType obectType, String proposedName);

        ArrayList columns = new ArrayList();

        DynamicObjectType dot = new DynamicObjectType("myTestObjectType3",
                                                      m_root.getObjectType
                                                      ("com.arsdigita.kernel.ACSObject"));

        ObjectType objectType = dot.getObjectType();
        Table table = objectType.getReferenceKey().getTable();

        // let's test to make sure that it gives back different columns
        for (int i = 0; i < 200; i++) {
            String columnName = m_generator.generateColumnName(objectType,
                                                               "myObjectName");
            assertTrue("generateColumn produced a duplicate column name: " +
                   columnName, !columns.contains(columnName.toLowerCase()));
            Property property = new Property("prop" + i,
                                             MetadataRoot.BIGDECIMAL,
                                             Property.REQUIRED);

            columns.add(columnName.toLowerCase());
            property.setColumn(new Column(table, columnName,
                                          java.sql.Types.INTEGER, 32));
            objectType.addProperty(property);
        }

        // now that we know that it does not put out duplicates, let's check
        // for length
        String columnName = m_generator.generateColumnName
            (objectType, "myReallyReallyLongObjectName");
        assertTrue("the column name passed back is too long",
               columnName.length() <= 26);
    }

    /**
     * Check that we can extend a non-integer keyed table
     */
    public void testNonIntegerPrimaryKey() {
        DynamicObjectType dot = new DynamicObjectType(
                                                      "subEmail",
                                                      m_root.getObjectType("com.arsdigita.kernel.EmailAddress"));

        dot.addOptionalAttribute("testAttr1", MetadataRoot.STRING);
        ObjectType type = dot.save();

        DataObject testObj = getSession().create(type);

        testObj.set("emailAddress", "bob@bob.bob");
        testObj.set("isBouncing", Boolean.FALSE);
        testObj.set("isVerified", Boolean.TRUE);
        testObj.set("testAttr1", "hello bob");

        testObj.save();

        testObj = getSession().retrieve(new OID(type, "bob@bob.bob"));

        assertTrue("Primary key not set",
               ((String)testObj.get("emailAddress")).equals("bob@bob.bob"));
        assertTrue("Test attribute not saved",
               ((String)testObj.get("testAttr1")).equals("hello bob"));

        testObj.delete();

        m_tables.add(type.getReferenceKey().getTableName());
        m_objectTypes.add(type);
    }

    /**
     *  This makes sure that the system generates the correct syntax
     *  for generating a column name to be added to an alter table
     *  or create table statement
     */
    /*
      public void testGenerateColumnToAdd() {
      DynamicObjectType dot = new DynamicObjectType("myTestObjectType4",
      m_root.getObjectType
      ("com.arsdigita.kernel.ACSObject"));
      dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
      dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
      Property prop = dot.addRequiredAttribute("requiredAttribute",
      MetadataRoot.BOOLEAN,
      new Boolean(true));
      dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
      "my metadataroot");

      String toAdd = m_generator.generateColumnToAdd(prop, null);
      assertNotNull("the string to remove a column should not be null",
      toAdd);

      // at the very least, the words "not null" should appear and
      // they should not be the first items in the list
      assertTrue("The column returned that is supposed to be added was not " +
      "correct.  It returned [" + toAdd + "]",
      toAdd.indexOf("not null") > 0 && toAdd.length() > 9);
      }
    */

    /**
     *  This makes sure that the system just returns the correct
     *  syntax that should be added when removing a column
     */
    /*
      public void testGenerateColumnToRemove() {
      DynamicObjectType dot = new DynamicObjectType("myTestObjectType2",
      m_root.getObjectType
      ("com.arsdigita.kernel.ACSObject"));
      dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
      dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
      Property prop = dot.addRequiredAttribute("requiredAttribute",
      MetadataRoot.BOOLEAN,
      new Boolean(true));
      dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
      "my new string");

      String toRemove = m_generator.generateColumnToRemove(prop);
      assertNotNull("the string to remove a column should not be null",
      toRemove);
      assertTrue("The column to remove is not correct",
      toRemove.equals(prop.getColumn().getColumnName()));
      }
    */


    /**
     *  This tests to make sure that it is possible to add a required
     *  column to an existing column
     */
    /*
      public void testRequiredAttributes() {
      DynamicObjectType dot = new DynamicObjectType("myTestObjectType6",
      m_root.getObjectType
      ("com.arsdigita.kernel.ACSObject"));
      dot.addOptionalAttribute("optionalAttribute", MetadataRoot.STRING, 300);
      dot.addOptionalAttribute("optionalAttribute2", MetadataRoot.BOOLEAN);
      ObjectType objectType = dot.save();

      // now create a data object and insert a row
      DataObject dataObject = SessionManager.getSession().create(objectType);
      try {
      dataObject.set("id", Sequences.getNextValue());
      dataObject.set("objectType", "my object type");
      dataObject.set("displayName", objectType.getQualifiedName());
      dataObject.set("optionalAttribute", "my string value");
      dataObject.save();
      } catch (SQLException e) {
      fail("a SQLException was thrown when it should not have been." +
      Utilities.LINE_BREAK + e.getMessage());
      }

      String defaultValue = "my new string";

      dot.addRequiredAttribute("requiredAttribute2", MetadataRoot.STRING, 200,
      defaultValue);
      dot.save();

      try {
      dot.addRequiredAttribute("requiredAttribute4", MetadataRoot.STRING,
      200, null);
      fail("adding a 'not null' column and setting it null with the " +
      "default value is not permitted");
      } catch (PersistenceException e) {
      // this is where it should be
      }

      // make sure the value for the item that we have is correct
      assertTrue("The default value should be what is retreived",
      defaultValue.equals((String)dataObject.get("requiredAttribute2")));
      m_tables.add(objectType.getReferenceKey().getTableName());
      m_objectTypes.add(objectType);

      Date date = new Date(Math.round(((new Date()).getTime()) -
      (1000*60*60*24*4.2)));
      Property prop = dot.addRequiredAttribute("myOptionalProp",
      MetadataRoot.DATE, 400,
      date);
      String col = m_generator.generateColumnToAdd(prop, date);
      s_log.info("COL = " + col);
      assertTrue("when adding a date, the default should be subtracted from the " +
      "default.  Instead, we have [" + col + "]",
      col.indexOf("-") > 0 && col.indexOf("4.") > 0 &&
      col.indexOf("default") > 0);
      date = new Date(Math.round(((new Date()).getTime()) +
      (1000*60*60*24*3.2)));

      Property prop2 = dot.addRequiredAttribute("myOptionalProp2",
      MetadataRoot.DATE, 400,
      date);
      col = m_generator.generateColumnToAdd(prop2, date);
      assertTrue("when adding a date, the default should be added to the " +
      "default.  Instead, we have [" + col + "]",
      col.indexOf("+") > 0 && col.indexOf("3.") > 0 &&
      col.indexOf("default") > 0);
      }
    */
}
