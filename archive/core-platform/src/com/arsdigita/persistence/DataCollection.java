/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
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
 * @version $Revision: #7 $ $Date: 2004/04/07 $
 *
 * @see com.arsdigita.persistence.SessionManager
 * @see com.arsdigita.persistence.Session
 * @see com.arsdigita.persistence.DataObject
 * @see com.arsdigita.persistence.DataQuery
 */

public interface DataCollection extends DataQuery {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataCollection.java#7 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
