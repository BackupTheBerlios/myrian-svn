/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * Title:       DataObject interface
 * Description: This interface defines the public methods of Data Objects.
 *              This interface is primarily how client code interacts with any
 *              GenericDataObject. The GenericDataObject class is primary
 *              used for use by the persistent engine and anyone wanting to
 *              extend the functionality of GenericDataObjects through
 *              inheritance.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 */

public interface DataObject {

    String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/DataObject.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    /**
     * Returns the type of this persistent object.
     *
     * @return The type of this persistent object.
     **/

    ObjectType getObjectType();

    /**
     * Returns the unique id of this persistent object.
     *
     * @return The id of this object.
     **/

    OID getOID();

    /**
     * Returns the value of the specified property.
     *
     * @param propertyName The property name.
     *
     * @return The property value.
     **/

    Object get(String propertyName);

    /**
     * Sets the specified property to <i>value</i>.
     *
     * @param propertyName The property name.
     * @param value The desired value.
     **/

    void set(String propertyName, Object value);

    /**
     * Returns the Session object from which this object was created or
     * retrieved.
     *
     * @return This object's Session.
     **/

    Session getSession();

    /**
     * Returns true if this persistent object is newly created.
     *
     * @return True if the object is newly created.
     **/

    boolean isNew();

    /**
     * Returns true if this persistent object has been deleted from
     * the database.  This does a database hit to check.
     *
     * @return True if the object has been deleted
     **/

    boolean isDeleted();

    /**
     * Returns true if the object exists in a committed state in the
     * database. This does not mean that all changes to this object have been
     * either written to disk or committed.
     *
     * @return True if the object exists in a committed state in the database.
     **/

    boolean isCommitted();

    /**
     * Returns true if this persistent object has been disconnected from
     * the transaction context. If true, the object can still be read, but
     * any attempt to update any of the object's attributes will cause an
     *  exception to be thrown.
     *
     * @return True if the object has been disconnected
     **/

    boolean isDisconnected();

    /**
     * Disconnects this DataObject from the current transaction. This allows
     * the data object to be used in multiple transactions.
     *
     * @see #isDisconnected()
     **/

    void disconnect();

    /**
     * Returns true if this persistent object has been modified since it was
     * retrieved.
     *
     * @return True if the object has been modified, false otherwise.
     **/

    boolean isModified();

    /**
     * Returns true of the property specified by <i>name</i> has been modified
     * since this object was retrieved.
     *
     * @return True if the property has been modified, false otherwise.
     **/

    boolean isPropertyModified(String name);

    /**
     *  Returns true if this persistent object is in a valid state.
     *  An invalid DataObject usually results from using a data object that was
     *  retrieved during a transaction that has been rolled back.
     *
     * @return True if the object has been modified, false otherwise.
     **/

    boolean isValid();

    /**
     * Deletes this persistent object.
     *
     * @post isDeleted()
     **/

    void delete();


    /**
     * Specializes this persistent object by turning it into a subtype of this
     * object's current type.
     *
     * @param subtype The subtype to which to specialize.
     *
     * @pre subType.isASuperType(getObjectType()) || subtype.equals(getObjectType())
     *
     * @post subtype.equals(getObjectType())
     **/

    void specialize(ObjectType subtype);

    /**
     * Specializes this persistent object by turning it into a subtype of this
     * object's current type. In addition to the local precondition, also
     * has pre and post conditions of specialize(ObjectType).
     *
     * @param subtypeName The name of the subtype to which to specialize.
     *
     * @pre SessionManager.getMetadataRoot().getObjectType(subtypeName) != null
     **/

    void specialize(String subtypeName);

    /**
     * Persists any changes made to this persistent object.
     *
     * @post !(isNew() || isModified())
     **/

    void save();


    /**
     * Adds an observer.
     *
     * @param observer The observer to add to this DataObject.
     **/

    void addObserver(DataObserver observer);


}
