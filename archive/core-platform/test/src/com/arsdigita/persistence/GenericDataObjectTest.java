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

package com.arsdigita.persistence;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Iterator;
import com.arsdigita.persistence.metadata.ObjectType;

/**
 * GenericDataObjectText
 *
 * This class tests GenericDataObject, using data contained in 
 * //enterprise/infrastructure/dev/persistence/sql/data-query-test.sql
 *
 *  This data must be loaded as a precondition of this test running.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */
public class GenericDataObjectTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/GenericDataObjectTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public GenericDataObjectTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/DataQuery.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
         
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/DataQuery.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl"); 
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        super.persistenceTearDown();
    }


    /**
     * This tests to make sure that a delete followed by a save throws
     * an exception
     */
    public void testCRUD() {
        DataQuery query = getSession().retrieveQuery("examples.nodesQuery");   
        long initialSize = query.size();

        DataObject node = getSession().create("examples.Node");
        node.set("id", new BigDecimal(0));
        node.set("name", "Root");
        node.save();
        // Should have no effect.
        node.save();
        node.save();
        node.save();


        // make sure it is there
        query = getSession().retrieveQuery("examples.nodesQuery");   
        assertEquals("The saving of a node did not actually save",
               initialSize + 1, query.size());

        node.delete();

        // make sure it is not there
        query = getSession().retrieveQuery("examples.nodesQuery");   
        assert("The deleting of a node did not actually delete.",
               query.size() == initialSize);  
        // should this be allowed?       
        node.delete(); 
        
        try {
            node.save();
            fail("saving a dataobject after deleting should cause and error");
        } catch (PersistenceException e) {
            //This should happen so we fall through
        }

    }

    /**
     * Test that SQL will indeed be issued to null-out a 0..1 association
     * even if the association has not previously been fetched.  See
     * bug report 145705
     *
     * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
     */
    public void testSetAssociationToNull() {
        DataObject parent = getSession().create("examples.Node");
        parent.set("id", new BigDecimal(42));
        parent.set("name", "Parent");
        parent.save();

        DataObject node = getSession().create("examples.Node");
        node.set("id", new BigDecimal(7));
        node.set("name", "Child");
        node.set("parent", parent);
        node.save();

        DataObject node2 = getSession().retrieve(
            new OID("examples.Node", new BigDecimal(7)));

        DataObject parent2 = (DataObject)node2.get("parent");

        assert("Parents not equal", parent.equals(parent2));

        DataObject node3 = getSession().retrieve(
            new OID("examples.Node", new BigDecimal(7)));

        // try erasing the parent.
        node3.set("parent", null);
        node3.save();

        parent2 = (DataObject)node3.get("parent");

        assert("Parent not set to null", parent2 == null);

    }


    

    public void testSpecialize() {
        DataObject node = getSession().create("examples.Node");
        try {
            node.specialize("grick!");
            fail("Specialized on a nonsensical ObjectType name!");    
        } catch (RuntimeException e) {
        }

        try {
            node.specialize( node.getObjectType() );
        } catch (RuntimeException e) {
            fail("Failed to Specialize on self!");
        }
        DataObject party = getSession().create("examples.Party");
        party.specialize("examples.User");


        party = getSession().create("examples.Party");
        party.set("id", BigInteger.ZERO);
        party.set("email", "jorris@arsdigita.com");
        party.save();
        
        ObjectType userType = getSession().create("examples.User").getObjectType();
        party.specialize( userType );
        party.set("firstName", "Jon");
        party.set("lastNames", "Orris");
        party.save();
        
        try {
            party.specialize("examples.Party");
            fail("Reversed specialization!");
        } catch (RuntimeException e) {
        }
           
    }

    /**
     *  This makes sure that if one of the items in the retrieve event
     *  fails then the returned object is null.
     */
    public void testFailingRetrieveQuery() {
        DataObject order = getSession().create
            ("examples.OrderExtWithFailingRetrieve");

        order.set("id", new BigDecimal(1));
        order.set("buyer", "Rafael H. Schloming");
        order.set("shippingAddress",
                  "102 R Inman St.\nCambridge MA, 02139");
        order.set("shippingDate",
                  new java.sql.Date(System.currentTimeMillis()));
        order.set("hasShipped", Boolean.FALSE);
        order.save();
        OID oid = order.getOID();

        order = getSession().retrieve(oid);
        assert("The retrieve method for [examples.OrderExtWithFailingRetrieve] " +
               "does not work so it should return null", order == null);


    }

     public static void main(String args[]) {
        junit.textui.TestRunner.run(GenericDataObjectTest.class);
    }

}








