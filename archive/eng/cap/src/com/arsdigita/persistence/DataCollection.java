/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * The DataCollection interface defines the public methods available on a
 * collection of DataObjects. DataCollections can be used to efficiently
 * iterate over a large set of DataObjects and access the values of their
 * properties. A DataCollection has much of the functionality of a
 * {@link com.arsdigita.persistence.DataQuery}, and can be filtered or sorted
 * in the same way. A typical usage of a DataCollection is:
 *
 *   <pre>
 *   Session ssn = SessionManager.getSession();
 *   DataCollection employees = ssn.retrieve("com.dotcom.Employee");
 *   employees.setFilter("name like '%nut'");
 *   employees.setOrder("name");
 *
 *   while (employees.next()) {
 *       System.out.println("ID: " + employees.get("id"),
 *                          "Name: " + employees.get("id"));
 *   }
 *   </pre>
 *
 * A DataCollection can also be used to fetch complete DataObjects as opposed
 * to simply accessing the property values of those DataObjects. This means of
 * access is less efficient than that described above because a Java object
 * must be instantiated for every DataObject in the DataCollection.
 *
 *   <pre>
 *   Session ssn = SessionManager.getSession();
 *   DataCollection employees = ssn.retrieve("com.dotcom.Employee");
 *   employees.addFilter("name like '%nut'");
 *   employees.addOrder("name");
 *
 *   while (employees.next()) {
 *       DataObject emp = employees.getDataObject();
 *       System.out.println(emp);
 *   }
 *   </pre>
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 *
 * @see com.arsdigita.persistence.SessionManager
 * @see com.arsdigita.persistence.Session
 * @see com.arsdigita.persistence.DataObject
 * @see com.arsdigita.persistence.DataQuery
 */

public interface DataCollection extends DataQuery {

    String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataCollection.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    /**
     * Returns a data object for the current position in the collection.
     *
     * @return A DataObject.
     **/

    DataObject getDataObject();

    /**
     * Returns the object type of the data collection.
     *
     * @return The object type of the data collection.
     **/

    ObjectType getObjectType();


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    void setParameter(String parameterName, Object value);


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @deprecated.  There is a raging debate about whether or not
     *  this should be deprecated.  One side says it should stay
     *  because it makes sense, the other side says it should go
     *  because Collections are types of Associations and that Associations
     *  should be pure "retrieve" calls without embedded parameters
     *  and the like.  If you find yourself using a parameter on
     *  a DataCollection, they you should rethink the design and maybe
     *  make it a DataQuery that returns DataObjects instead of
     *  a DataCollection.
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName);

    /**
     * Tests whether the current collection contains an object.
     *
     * @param oid The oid of the object.
     * @return True if the collection contains the object, false otherwise.
     */
    public boolean contains(OID oid);

    /**
     * Tests whether the current collection contains an object.
     *
     * @param data The dataobject.
     * @return True if the collection contains the object, false otherwise.
     */
    public boolean contains(DataObject data);


}
