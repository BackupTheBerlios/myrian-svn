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

import com.arsdigita.persistence.*;
import com.arsdigita.util.StringUtils;
import junit.framework.TestCase;
import java.util.*;
import java.math.BigDecimal;
import org.apache.log4j.Category;

/**
 * 
 * This class performs unit tests on com.arsdigita.persistence.metadata.OracleMDSQLGenerator </p>
 *
 * author <a href="mailto:jorriarsdigita.com">jorriarsdigita.com</a>
 * version $Revision: #1 $ $Date: 2002/05/12 $
 * 
 */

public class OracleMDSQLGeneratorTest extends PersistenceTestCase {  

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/metadata/OracleMDSQLGeneratorTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static Category s_log = 
        Category.getInstance(OracleMDSQLGeneratorTest.class.getName());

    public OracleMDSQLGeneratorTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        super.persistenceSetUp();
        load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
        load("com/arsdigita/persistence/pdl/aggressiveLoad.pdl");
    }

    protected void persistenceTearDown() {
        super.persistenceTearDown();
    }


    public void testAll() {
        ObjectType type =  MetadataRoot.getMetadataRoot().getObjectType("mdsql.Order");
        checkRetrieve(type);
        checkRetrieveAll(type);
        checkInsert(type);
        checkUpdate(type);
        checkDelete(type);
    }

    public void testGenerateRetrieve() {
        ObjectType type = makeDefaultObjectType();
        checkRetrieve(type);
    }

    private void checkRetrieve(ObjectType type) {
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event e = gen.generateRetrieve(type);

        Iterator iter = e.getOperations();
        while(iter.hasNext()) {
            Operation op = (Operation) iter.next();
            checkRetrieveOperation(op, type);
        }
        
    }

    private void checkRetrieveOperation(Operation op, ObjectType type) {
        s_log.info("Operation: " + op);

        checkSelect(op, type);
        checkWhere(op, type);
        checkMappings(op.getMappings(), type);
            
    }


    public void testGenerateRetrieveAll() {
        ObjectType type = makeDefaultObjectType();
        checkRetrieveAll(type);
    }

    private void checkRetrieveAll(ObjectType type) {
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event e = gen.generateRetrieveAll(type);

        Iterator iter = e.getOperations();
        while(iter.hasNext()) {
            Operation op = (Operation) iter.next();
            checkRetrieveAllOperation(op, type);
        }
        
    }

    private void checkRetrieveAllOperation(Operation op, ObjectType type) {
        s_log.info("Operation: " + op);

        checkSelect(op, type);
        checkMappings(op.getMappings(), type);
        final String sql = op.getSQL();
//        assertEquals(sql, -1, sql.indexOf("where"));
        
    }

    public void testInsert() {
        ObjectType type = makeDefaultObjectType();
        checkInsert(type);
        
    }

    private void checkInsert(ObjectType type) {
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event e = gen.generateInsert(type);
        s_log.info("Insert!");
        s_log.info(e);
        Iterator iter = e.getOperations();
        while(iter.hasNext()) {
            Operation op = (Operation) iter.next();
            checkInsertOperation(op, type);
        }
        
    }

    private void checkInsertOperation(Operation op, ObjectType type) {
        final String sql = op.getSQL();
        assertTrue( sql, sql.indexOf("insert ") != -1 );
        int beginColumns = sql.indexOf("(") + 1;
        int endColumns = sql.indexOf(")");
        final String columnStr = sql.substring(beginColumns, endColumns).trim();

        String valueSection = sql.substring(sql.indexOf("values"));
        final int beginValues = valueSection.indexOf("(") + 1;
        final int endValues = valueSection.indexOf(")");
        final String valueStr = valueSection.substring(beginValues, endValues).trim();
        String[] columns = StringUtils.split(columnStr, ',');
        String[] values = StringUtils.split(valueStr, ',');
        assertEquals("Not as many columns as values!!!", columns.length, values.length);
        for (int idx = 0;idx < columns.length; idx++) {

            String propertyName = values[idx].replace(':',' ').trim();
            Property prop = type.getProperty(propertyName);
            assertNotNull(propertyName + "not found!", prop);
            Column column = prop.getColumn();
            assertEquals(column.getColumnName(), columns[idx].trim());
        }
    }
    
    public void testDelete() {
        ObjectType type = makeDefaultObjectType();
        checkDelete(type);
    }

    private void checkDelete(ObjectType type) {
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event e = gen.generateDelete(type);
        s_log.info("Delete!");
        s_log.info(e);
        Iterator iter = e.getOperations();
        while(iter.hasNext()) {
            Operation op = (Operation) iter.next();
            checkDeleteOperation(op, type);
        }
        
    }

    private void checkDeleteOperation(Operation op, ObjectType type) {
        s_log.info("Operation: " + op);

        checkWhere(op, type);
        assertTrue("No delete!", op.getSQL().indexOf("delete from") != -1);    
    }

    public void testUpdate() {
        ObjectType type = makeDefaultObjectType();
        checkUpdate(type);
    }



    private void checkUpdate(ObjectType type) {
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event e = gen.generateUpdate(type);
        s_log.info("Update!");
        s_log.info(e);
        
    }

    public void testAggressiveLoading() {
        ObjectType type = makeDefaultObjectType();
        OracleMDSQLGenerator gen = (OracleMDSQLGenerator) MDSQLGeneratorFactory.getInstance();
        Event retrieve = gen.generateRetrieve(type);

        assert("No retrieve event created", retrieve != null);

        Iterator it = retrieve.getOperations();

        assert("No operations created", it.hasNext());

        Operation op = (Operation)it.next();

        assert("Too many operations created", !it.hasNext());

        it = op.getMappings();
        Column aggCol = null;

        while (it.hasNext()) {
            Mapping map = (Mapping)it.next();
            String mapName = StringUtils.join(map.getPath(),'.');

            if (mapName.equalsIgnoreCase("rr1.displayName")) {
                aggCol = map.getColumn();
                break;
            }
        }

        assert("Aggressively loaded attribute not found", aggCol != null);
    }

    private void assertAttributeValue(DataObject obj,
                                      String attrName,
                                      Object expected) {
        assert("Object is null", obj != null);

        StringTokenizer tokens = new StringTokenizer(attrName, ".");
        Object value = obj;

        while (tokens.hasMoreTokens()) {
            value = ((DataObject)value).get(tokens.nextToken());
        }

        assert(attrName + " was {" + value + "}, not {" + expected + "}",
               value.equals(expected));
    }

    public void testAggressiveLoadingPDL() {
        DataObject color1 = getSession().create("aggressiveLoad.Color");
        color1.set("id", new BigDecimal(1));
        color1.set("name", "Magnetic Red II");
        color1.save();

        DataObject color2 = getSession().create("aggressiveLoad.Color");
        color2.set("id", new BigDecimal(2));
        color2.set("name", "Electron Blue");
        color2.save();

        DataObject color3 = getSession().create("aggressiveLoad.Color");
        color3.set("id", new BigDecimal(3));
        color3.set("name", "Millennium Yellow");
        color3.save();

        DataObject user1 = getSession().create("aggressiveLoad.User");
        user1.set("id", new BigDecimal(1));
        user1.set("name", "David Eison");
        user1.set("favColor", color1);
        user1.save();
        OID oid1 = user1.getOID();

        DataObject user2 = getSession().create("aggressiveLoad.User");
        user2.set("id", new BigDecimal(2));
        user2.set("name", "Patrick McNeill");
        user2.set("favColor", color2);
        user2.set("referer", user1);
        user2.save();
        OID oid2 = user2.getOID();

        DataObject user3 = getSession().create("aggressiveLoad.User");
        user3.set("id", new BigDecimal(3));
        user3.set("name", "Randy Graebner");
        user3.set("favColor", color3);
        user3.set("referer", user2);
        user3.save();
        OID oid3 = user3.getOID();

        s_log.warn("XXX");
        user1 = getSession().retrieve(oid1);
        s_log.warn("YYY");
        user2 = getSession().retrieve(oid2);
        user3 = getSession().retrieve(oid3);

        assertAttributeValue(user1, "favColor.name", "Magnetic Red II");
        assertAttributeValue(user2, "favColor.name", "Electron Blue");
        assertAttributeValue(user3, "favColor.name", "Millennium Yellow");
        assertAttributeValue(user2, "referer.name", "David Eison");
        assertAttributeValue(user3, "referer.name", "Patrick McNeill");
        assertAttributeValue(user3, "referer.referer.name", "David Eison");
        assert("referer.name not null in user 1", user1.get("referer") == null);
    }
    
    /**
     * Makes an ObjectType with properties for every SimpleType in MetadataRoot.
     *
     *
     */
    private ObjectType makeDefaultObjectType() {
        ObjectType type = new ObjectType("RetTest");

        addProperty(type, "prop1", MetadataRoot.BIGINTEGER, "theTable", "column1");
        addProperty(type, "prop2", MetadataRoot.BIGDECIMAL, "theTable", "column2");
        addProperty(type, "prop3", MetadataRoot.BOOLEAN, "theTable", "column3");
        addProperty(type, "prop4", MetadataRoot.BYTE, "theTable", "column4");
        addProperty(type, "prop5", MetadataRoot.CHARACTER, "theTable", "column5");
        addProperty(type, "prop6", MetadataRoot.DATE, "theTable", "column6");
        addProperty(type, "prop7", MetadataRoot.DOUBLE, "theTable", "column7");
        addProperty(type, "prop8", MetadataRoot.FLOAT, "theTable", "column8");
        addProperty(type, "prop9", MetadataRoot.INTEGER, "theTable", "column9");
        addProperty(type, "prop10", MetadataRoot.LONG, "theTable", "column10");
        addProperty(type, "prop11", MetadataRoot.SHORT, "theTable", "column11");
        addProperty(type, "prop12", MetadataRoot.STRING, "theTable", "column12");
        addProperty(type, "prop13", MetadataRoot.BLOB, "theTable", "column13");
        addProperty(type, "prop14", MetadataRoot.CLOB, "theTable", "column14");
        addRoleReference(
            type,
            "rr1",
            MetadataRoot.getMetadataRoot()
                        .getObjectType("com.arsdigita.kernel.ACSObject"),
            "theTable",
            "theColumn");

        type.addAggressiveLoad(new String[] {"rr1", "displayName"});

        type.addKeyProperty("prop1");

        return type;
        
    }

    private void addProperty(ObjectType type, 
                             String propertyName, 
                             SimpleType dataType,
                             String tableName,
                             String columnName) {

        Property prop = new Property(propertyName, dataType);
        Column column = new Column(tableName, columnName, dataType.getJDBCtype());
        prop.setColumn(column);
        type.addProperty(prop);
                                    
    }
    
    private void addRoleReference(ObjectType type,
                                  String refName,
                                  ObjectType refType,
                                  String tableName,
                                  String columnName) {
        Property prop = new Property(refName, refType);
        JoinPath jp = new JoinPath();

        Column end = Utilities.getColumn(refType);
        Column start = new Column(tableName, columnName, end.getType());
        jp.addJoinElement(start, end);

        prop.setJoinPath(jp);

        type.addProperty(prop);
    }


    /**
     *  Checks that the given Operation is a select operation,
     *  and that it contains all the columns for the ObjectType.
     *
     *  @param op The SQL Operation.
     *  @param type The ObjectType
     */
    private void checkSelect(Operation op, ObjectType type) {
        final String sql = op.getSQL();
        assertTrue( sql, sql.indexOf("select") != -1 );
        Iterator properties = type.getProperties();
        checkForColumnNames( sql, properties );

    }

    /**
     *  Checks the where clause of op's sql statement. Verifies that
     *  all of type's keyProperties are contained in the where clause.
     *
     */
    private void checkWhere(Operation op, ObjectType type) {
        final String sql = op.getSQL();
        final int whereIdx = sql.indexOf("where");
        assertTrue( sql, whereIdx != -1 );

        final String whereClause = sql.substring(whereIdx);
        Iterator keyProperties = type.getKeyProperties();
        checkForColumnNames( whereClause, keyProperties );
        
    }

    /**
     *  Given an SQL statement and an iterator over a set of
     *  Property objects, checks to see that the Property's Column
     *  is in the statement.
     *
     *  @param sql The sql statement
     *  @parma iter An iterator over a collection of Property objects.
     */
    private void checkForColumnNames(String sql, Iterator iter) {
        while (iter.hasNext()) {
            Property prop = (Property) iter.next(); 
            if( prop.isAttribute() ) {
                Column column = prop.getColumn();
                if (null == column) {
                    fail("no column for property: " + prop.getName());
                
                }
                final boolean statementContainsColumn = (sql.indexOf(column.getColumnName()) != -1);
                assertTrue( sql, statementContainsColumn ); 
                
            }
        }

    }

    /**
     *  Foreach Mapping in mappings, check to see if the Column is mapped
     *  to the correct Property in the ObjectType. Also verifies that the
     *  column and property names are contained in the PDL statement of the Mapping.
     *
     *  Removed (temporarily?) -- column renaming makes it so this really
     *  doesn't work anymore.
     */
    private void checkMappings(Iterator mappings, ObjectType type) {
/*
        while(mappings.hasNext()) {
            Mapping map = (Mapping) mappings.next();
            final String fullPropertyName = StringUtils.join(map.getPath(),'.');
            Column column = map.getColumn();
            Property property = type.getProperty(fullPropertyName);

            // skip anything that's aggressively loaded
            if (property == null) {
                continue;
            }

            assertEquals( property.getColumn(), column );
            final String pdlStatement = map.toString();
            assertTrue( pdlStatement + "\ndoes not contain " + fullPropertyName,
                 pdlStatement.indexOf(fullPropertyName) != -1 );
            final String fullColumnName = column.getTableName() + "." + column.getColumnName();
            assertTrue( pdlStatement + "\ndoes not contain " + fullColumnName,
                 pdlStatement.indexOf(fullColumnName) != -1 ); 

        }
*/        
    }
}
