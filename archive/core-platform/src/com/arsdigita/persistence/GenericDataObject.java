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

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.util.Assert;
import java.sql.Connection;

import java.math.BigDecimal;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Title:       GenericDataObject class
 * Description: This class provides the functionality for entities that are
 *              stored in a relational database. It manages state and has
 *              methods for reading and writing to a database. The GenericDataObject
 *              class is primary used for use by the persistent engine and
 *              anyone wanting to extend the functionality of GenericDataObjects
 *              through inheritance.
 * Copyright:    Copyright (c) 2001
 * Company:      ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/05/30 $
 */

public class GenericDataObject implements DataObject {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/GenericDataObject.java#3 $ by $Author: rhs $, $DateTime: 2002/05/30 17:55:49 $";

    private ObjectType    m_type;
    private Session       m_session;
    private DataContainer m_data = new DataContainer();
    private boolean m_isDeleted = false;
    private boolean m_valid = true;
    private boolean m_disconnected = false;
    private boolean m_isNew = false;
    
    // The object type of the data object at the time that doRetrieve(OID)
    // is executed.  If the dataObject is specialized to a subtype, then
    // m_type will be different from m_retrievedType.
    // If m_retrievedType is null, then doRetrieve() was never executed,
    // which means the dataObject was created, not retrieved.  However,
    // the meaning of m_retrievedType==null is different than the meaning
    // of isNew()==true.  If the dataObject was created and saved, then
    // isNew() will be false, but m_retrievedType will be null.
    private ObjectType    m_retrievedType;

    private Set m_observers = new HashSet();

    private static final class ObserverEntry {

        private DataObserver m_observer;
        private int m_index;

        ObserverEntry(DataObserver observer, int index) {
            m_observer = observer;
            m_index = index;
        }

        public DataObserver getObserver() {
            return m_observer;
        }

        public int getIndex() {
            return m_index;
        }

        public int hashCode() {
            return m_observer.hashCode();
        }

        public boolean equals(Object other) {
            if (other instanceof ObserverEntry) {
                return m_observer.equals(((ObserverEntry) other).m_observer);
            } else {
                return super.equals(other);
            }
        }

    }

    private static final DataHandler s_defaultHandler = new DataHandler() {};

    private DataHandler m_dataHandler = s_defaultHandler;

    /**
     * @see DataObject.setDataHandler(DataHandler handler)
     **/

    public DataHandler setDataHandler(DataHandler handler) {
        DataHandler result = m_dataHandler;
        m_dataHandler = handler;
        return result;
    }

    /**
     * @see DataObject.addObserver(DataObserver observer)
     **/

    public void addObserver(DataObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Can't add a null observer.");
        }
        ObserverEntry entry = new ObserverEntry(observer, m_observers.size());
        if (!m_observers.contains(entry)) {
            if (m_firing) {
                throw new IllegalStateException(
                    "Can't add an observer from within an observer."
                    );
            }
            m_observers.add(entry);
        }
    }

    private static final int BEFORE_SAVE = 0;
    private static final int AFTER_SAVE = 1;
    private static final int BEFORE_DELETE = 2;
    private static final int AFTER_DELETE = 3;
    private static final int NUM_METHODS = 4;

    private int[] m_count = new int[NUM_METHODS];
    private BitSet[] m_fired = new BitSet[NUM_METHODS];
    private boolean m_firing = false;

    private void fireObserver(int method, boolean errorOnSave) {
        if (m_count[method] == 0) {
            m_fired[method] = new BitSet(m_observers.size());
        }

        m_count[method]++;
        boolean old = m_firing;
        m_firing = true;

        try {
            for (Iterator it = m_observers.iterator(); it.hasNext(); ) {
                ObserverEntry entry = (ObserverEntry) it.next();
                DataObserver observer = entry.getObserver();
                int index = entry.getIndex();
                if (m_fired[method].get(index)) {
                    if (errorOnSave) {
                        throw new PersistenceException(
                            "Loop detected while firing a DataObserver. " +
                            "This probably resulted from calling the save " +
                            "method of a data object from within a " +
                            "beforeSave observer registered on that " +
                            "same data object, or the analogous situation " +
                            "with delete and beforeDelete/afterDelete."
                            );
                    }
                } else {
                    m_fired[method].set(index);
                    callObserverMethod(observer, method);
                }
            }
        } finally {
            m_count[method]--;
            m_firing = old;
        }

        if (m_count[method] == 0) {
            m_fired[method] = null;
        }
    }

    private void callObserverMethod(DataObserver observer, int method) {
        switch (method) {
        case BEFORE_SAVE:
            observer.beforeSave(this);
            break;
        case AFTER_SAVE:
            observer.afterSave(this);
            break;
        case BEFORE_DELETE:
            observer.beforeDelete(this);
            break;
        case AFTER_DELETE:
            observer.afterDelete(this);
            break;
        default:
            throw new IllegalStateException(
                "No such observer method: " + method
                );
        }
    }

    private void fireBeforeSave() {
        fireObserver(BEFORE_SAVE, true);
    }

    private void fireAfterSave() {
        fireObserver(AFTER_SAVE, false);
    }

    private void fireBeforeDelete() {
        fireObserver(BEFORE_DELETE, true);
    }

    private void fireAfterDelete() {
        fireObserver(AFTER_DELETE, true);
    }

    void setNew(boolean isNew) {
        m_isNew = isNew;
    }

    /**
     *  This sets the object type of this object
     *  
     *  @param type The ObjectType for this DataObject
     */
    void setObjectType(ObjectType type) {
        m_type = type;
        m_data.setType(m_type);
    }


    /**
     *  Sets the database session for this object.
     *
     *  @param session The database session to use.
     */
    void setSession(Session session) {
        m_session = session;
    }


    /**
     * Returns the DataStore object that this GenericDataObject should use for
     * I/O. This is intended for use by subtypes of GenericDataObject.
     *
     * @return A DataStore object.
     **/

    protected DataStore getDataStore() {
        return SessionManager.getSession().getDataStore();
    }


    /**
     * Returns the DataContainer used to store the properties of this
     * GenericDataObject. This is intended for use by subtypes of
     * GenericDataObject.
     *
     * @return The DataContainer used for this object's properties.
     **/
    protected DataContainer getDataContainer() {
        return m_data;
    }


    /**
     * Returns the type of this data object.
     *
     * @return The type of this data object.
     **/
    public ObjectType  getObjectType() {
        validate();

        return m_type;
    }


    /**
     * Specializes this data object by turning it into a subtype of this
     * object's current type.
     *
     * @param subtype The subtype to which to specialize.
     *
     * @pre subType.isASuperType(getObjectType()) || subtype.equals(getObjectType()) 
     *
     * @post subtype.equals(getObjectType())
     **/
    public void specialize(ObjectType subtype) {
        validate();

        ObjectType.verifySubtype(m_type, subtype);
        setObjectType(subtype);
    }


    /**
     * Specializes this persistent object by turning it into a subtype of this
     * object's current type. In addition to the local precondition, also
     * has pre and post conditions of specialize(ObjectType).
     *
     * @param subtypeName The name of the subtype to which to specialize.
     *
     * @pre SessionManager.getMetadataRoot().getObjectType(subtypeName) != null
     **/
    public void specialize(String subtypeName) {
        validate();

        if (subtypeName == null) {
            throw new PersistenceException("Null value passed to specialize.");
        }

        ObjectType subtype = 
            MetadataRoot.getMetadataRoot().getObjectType(subtypeName);

        if (subtype == null) {
            throw new PersistenceException(
                "No such type: " + subtypeName
                );
        }

        specialize(subtype);
    }


    /**
     * Writes any modifications made to this object to the persistence
     * mechanism used to store the object.
     *
     * @exception PersistenceException thrown if 
     * {@link #delete()} has been called on this object.
     **/
    public void save() throws PersistenceException {
        validate(true);

        fireBeforeSave();

        if (m_isDeleted) {
            throw new PersistenceException("The object you are trying to " +
                                           "save (OID: " + getOID() + " has " +
                                           "already been deleted.");
        } else if (isNew()) {
            doInsert();
            setNew(false);
        } else if (isModified()) {
            doUpdate();
        }

        pushMessage("Saving associations for " + getOID());
        saveAssociations();
        popMessage();
        m_data.sync();

        fireAfterSave();
    }


    /**
     * Deletes this data object from the persistence mechanism used to store
     * it.
     **/
    public void delete() throws PersistenceException {

        validate(true);

        fireBeforeDelete();

	saveAssociations();

        // Delete any components
        for (Iterator it = m_type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (prop.isRole() && prop.isComponent()) {
                if (prop.isCollection()) {
                    DataAssociationImpl da =
                        (DataAssociationImpl) m_data.get(prop.getName());
                    if (da != null) {
                        da.delete();
                    }
                } else {
                    GenericDataObject obj =
                        (GenericDataObject) m_data.get(prop.getName());
                    if (obj != null) {
                        doRoleRemove(prop.getName(), obj.m_data);
                        obj.delete();
                    }
                }
            }
        }

        m_dataHandler.doDelete(this);

        fireAfterDelete();
    }


    /**
     * Returns the unique id of this object.
     *
     * @return The id of the object.
     **/
    public OID getOID() {
        validate();

        OID oid = new OID(m_type);

        for (Iterator it = m_type.getKeyProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            String name = prop.getName();
            oid.set(name, m_data.get(name));
        }

        return oid;
    }


    /**
     * Returns the value of the property specified by <i>name</i>. If
     * this property has already been retrieved, we return a reference
     * to the same object. An exception is made for role references
     * with upper bound > 1. In this case, we return a new instance of
     * a DataAssociation.
     *
     * @param propertyName The name of the property.
     * @return The value of the property.
     **/
    public Object get(String propertyName) throws PersistenceException {
        validate();

        Property prop = checkProperty(propertyName);

        // We want to return the cached value, if available if
        // 1. the value is a standard scalar attribute (not a RoleReference)
        // 2. the value is a RoleReference with Multiplicity upper bound
        //    greater than 1 AND the cached value is null
        // 3. the value is a RoleReference with Multiplicity upper bound
        //    of 1 and the property has NOT been modified.  We reretrieve
        //    the unmodified value because it is possible that the other
        //    end of the association changed
//        if (m_data.hasProperty(propertyName) && 
//            !(prop.isRole() && 
//              ((prop.isCollection() && m_data.get(propertyName) == null) ||
//               (!prop.isCollection() &&
//                !m_data.isPropertyModified(propertyName))))) {
        if (m_data.hasProperty(propertyName)) {
            return m_data.get(propertyName);
        }

        if (prop.isRole()) {
            if (!prop.isCollection()) {
                if (isNew()) {
                    return null;
                }

                // We need to do this in case the role is being refetched.
                m_data.clearProperty(propertyName);
                if (!doRoleRetrieve(propertyName, (DataContainer)null)) {
                    m_data.initProperty(propertyName, null);
                }
                return m_data.get(propertyName);
            } else {
                Event event = prop.getEvent(Property.RETRIEVE);
                if (event == null) {
                    throw new PersistenceException(
                        "No retrieve event defined for " +
                        "property '" + prop.getName() +
                        "' of object type '" + m_type.getQualifiedName() +
                        "' " + m_type.toString()
                        );
                }
                Operation op = (Operation) event.getOperations().next();
                DataAssociationImpl assn =
                    new DataAssociationImpl(this, propertyName, op);
                m_data.initProperty(propertyName, assn);
                return assn;
            }
        } else {
            if (isNew()) {
                return null;
            }

            // Load the attribute on demand.
            // First try just retrieving the attribute
            if (! doRetrieve(propertyName) ) {
                // doRettrieve(Attribute) failed.
                // We now try to do a full retrieve of the data object if 
                // either of the following is true:
                //    - a full retrieve was never executed.  This could be the
                //      case if this data object was new and then saved.
                //      In this case, m_retrieveType will be null.
                // OR
                //    - this data object was retrieved as one type, then
                //      specialized to a subtype.
                // In either case, m_retreivedType!=m_type

                if (m_retrievedType != m_type) {

                    // Do the full retrieve.
                    if (! doRetrieve(getOID()) ) {

                        // full retrieve failed

                        // I removed the "no retrieve attr event defined"
                        // exception from StaticEventBuilder.doRetrieve(attr)
                        // For now, we throw the exception here.
                        // TO DO: talk to Rafi about how he really wants
                        // to handle this.

                        throw new PersistenceException(
                                  "Unable to retrieve attribute " + 
                                  propertyName +
                                  " of object type " + m_type.getName() +
                                  " because there is no retrieve attribute " +
                                  " event handler defined for it and " +
                                  " the retrieve object event returned no " +
                                  " data."
                                  );
                    }
                }
            }

            // If we get here, then either doRetrieve(Attribute) or 
            // doRetrieve(OID) succeeded (meaning that some sqlblock was 
            // executed).
            return m_data.get(propertyName);
        }
    }


    /**
     * Sets the property specified by <i>name</i> to <i>value</i>
     *
     * @param name The name of the property.
     * @param value The desired value.
     **/
    public void set(String propertyName, Object value) {
        validate(true);

        // if value is an object or scalar then it adds to data container
        Property prop = checkProperty(propertyName);

        // make sure that the property is loaded so it can be nulled correctly
        // this is a bit of a hack, but the correct solution is much harder
        // and not very well supported.
        if (value == null && (prop.isRole() && !prop.isCollection())) {
            get(propertyName);
        }

        m_data.set(propertyName, value);
    }


    /**
     * Returns the Session object to which this GenericDataObject belongs.
     *
     * @return The Session of this object.
     **/
    public Session getSession() {
        return m_session;
    }


    /**
     * Returns true if this data object has been newly created.
     *
     * @return True if the object is newly created.
     **/
    public boolean isNew() {
        validate();
        return m_isNew;
    }


    /**
     * Returns true if this persistent object has been deleted from 
     * the database.  This does a database hit to check.
     *
     * @return True if the object has been deleted
     **/
    public boolean isDeleted() {
        validate();
        return m_isDeleted;
    }

    /**
     * Returns true if this persistent object has been disconnected from 
     * the transaction context. If true, the object can still be read, but 
     * any attempt to update any of the object's attributes will cause an 
     *  exception to be thrown.
     *
     * @return True if the object has been disconnected
     **/

    public boolean isDisconnected() {
        return m_disconnected;
    }


    /**
     * Returns true if this data object has been modified.
     *
     * @return True if modified.
     **/
    public boolean isModified() {
        validate();

        return m_data.isModified();
    }


    /**
     * Returns true if the property specified by <i>name</i> has been modified
     * since this object was retrieved.
     *
     * @param name The name of the property.
     *
     * @return True of the property was modified.
     **/
    public boolean isPropertyModified(String name) {
        validate();

        return m_data.isPropertyModified(name);
    }

    /**
     *  Returns true if this persistent object is in a valid state.
     *  An invalid DataObject usually results from using a data object that was 
     *  retrieved during a transaction that has been rolled back.
     *
     * @return True if the object has been modified, false otherwise.
     **/

    public boolean isValid() {
        return m_valid;
    }

    /**
     * Queries for the existence of a property.
     *
     * @param name The name of the property.
     * @return True if the data object has the specified property, false
     *         otherwise.
     */
    protected boolean hasProperty(String name) {
        validate();

        return m_type.hasProperty(name);
    }


    /**
     *   This saves that associations for this GenericDataObject
     */
    private void saveAssociations() throws PersistenceException {
        validate(true);

	// This function is getting way to long and harry. It would be quite
	// possible to rewrite the loops as one hopefully simpler loop, but I
	// don't want to do that a couple minutes before the formal build, so
	// I'll just note that this function should be dramatically
	// simplified. rhs@mit.edu

        // Loop through all data associations and perform any necessary
        // operations. This process is the same for both updating and
        // inserting.
        for (Iterator it = m_data.getDataAssociations(); it.hasNext(); ) {
            DataAssociationImpl da = (DataAssociationImpl) it.next();
            if (da == null) { continue; }
            da.save();
        }

        Map old = m_data.getBack();

        for (Iterator it = m_data.getFront().entrySet().iterator();
             it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            String key = (String) me.getKey();
            Object value = me.getValue();

            Property prop = m_type.getProperty(key);
            if (prop.isRole()) {
                GenericDataObject toRemove = (GenericDataObject) old.get(key);
                GenericDataObject toAdd;
                try {
                    toAdd = (GenericDataObject) value;
                } catch (ClassCastException e) {
                    throw new PersistenceException(
                        "Expecting the " + key + " property of " +
                        m_type.getQualifiedName() + " to be set to a DataObject, " +
                        "but found " + value.getClass().getName() + "."
                        );
                }

                if (toAdd != null && toAdd.equals(toRemove)) {
                    continue;
                }

                if (toRemove != null) {
                    doRoleRemove(key, toRemove.m_data);
                    if (prop.isComponent()) {
                        toRemove.delete();
                    }
                }

                if (toAdd != null) {
                    if (prop.isComponent()) {
                        toAdd.save();
                    }
                    doRoleAdd(key, toAdd.m_data);
                }
            }
        }

	for (Iterator it = m_type.getProperties(); it.hasNext(); ) {
	    Property prop = (Property) it.next();
	    if (prop.isRole() && prop.isComponent() && !prop.isCollection()) {
		GenericDataObject toSave =
		    (GenericDataObject) m_data.get(prop.getName());
		if (toSave != null && !toSave.m_isDeleted) {
		    toSave.save();
		}
	    }
	}
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GenericDataObject:" + Utilities.LINE_BREAK)
            .append(" + OID = " + getOID() + Utilities.LINE_BREAK)
            .append(" + Properties: " + Utilities.LINE_BREAK);

        Iterator iter = m_type.getProperties();
        while (iter.hasNext()) {
            Property prop = (Property) iter.next();
            sb.append("   - " + prop.getName() + Utilities.LINE_BREAK);
        }
    
        return sb.toString();
    }
    
    /**
     * Overridable methods for specializing Data Objects. *
     **/

    /**
     * This method is called to populate the data container of a newly created
     * Data Object instance with the values from an existing object stored
     * in the persistence mechanism. The <i>oid</i> contains the value that
     * was passed into Session.retrieve(). This method returns true if the
     * data container was successfully populated from the persistence
     * mechanism, false otherwise.
     *
     * @param oid The unique id of the Data Object.
     *
     * @return True if successful, false otherwise.
     **/

    protected boolean doRetrieve(OID oid) throws PersistenceException {
        validate();

        // Record the object type used for doing the retrieve.  This
        // info is used later if this data object is specialized.
        m_retrievedType = m_type;

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        return getDataStore().doRetrieve(m_type, oid, m_data);
    }

    /**
     * This method is called in order to save all the single valued attributes
     * associated with a newly created GenericDataObject (i.e. an object that
     * doesn't already exist in the persistence mechanism).
     **/

    protected void doInsert() throws PersistenceException  {
        validate(true);

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Inserting information for " + getOID());
        getDataStore().doInsert(m_type, m_data);
        popMessage();
    }

    /**
     * This method is called in order to save modifications to single valued
     * attributes of a Data Object that already exists in the persistence
     * mechanism.
     **/

    protected void doUpdate() throws PersistenceException {
        validate(true);

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Updating information for " + getOID());
        getDataStore().doUpdate(m_type, m_data);
        popMessage();
    }

    /**
     * This method is called in order to remove the Data Object from the
     * persistence mechanism.
     **/

    protected void doDelete() throws PersistenceException {
        validate(true);

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Deleting information for " + getOID());
        getDataStore().doDelete(m_type, m_data);
        m_isDeleted = true;
        popMessage();
    }

    /**
     * This method is called when an attribute is accessed, but isn't in
     * memory, and so needs to be loaded from the database.
     *
     * @param attr The attribute that has been requested.
     **/

    protected boolean doRetrieve(String attr) {
        validate();

        pushMessage("Retrieving information for attribute " + attr +
                    " " + getOID());
        boolean toReturn = getDataStore().doRetrieve(m_type, attr, m_data);
        popMessage();
        return toReturn;
    }

    /**
     * This method is called when an object has been added to a role where
     * that role has a multiplicity with an upper bound of one or less. The
     * state of the object being added is stored in <i>dc</i>.
     *
     * @param roleName The name of the role to which the object is being
     *                 added.
     * @param dc The DataContainer of the object being added.
     **/

    protected void doRoleAdd(String roleName, DataContainer dc)
        throws PersistenceException {
        validate(true);

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Adding role " + roleName + " to " + getOID());
        getDataStore().doRoleAdd(m_type, roleName, m_data, dc);
        popMessage();
    }

    /**
     * This method is called when an object has been removed from a role where
     * that role has a multiplicity with an upper bound of one or less. The
     * state of the object being removed is stored in <i>dc</i>.
     *
     * @param roleName The name of the role from which the object is being
     *                 removed.
     * @param dc The DataContainer of the object being removed.
     **/

    protected void doRoleRemove(String roleName, DataContainer dc)
        throws PersistenceException {
        validate();

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Removing role " + roleName + " to " + getOID());
        getDataStore().doRoleRemove(m_type, roleName, m_data, dc);
        popMessage();
    }

    /**
     * This method is called when all objects are to be removed from a role
     * where that role has a multiplicity with an upper bound of one or less.
     **/

    protected void doRoleClear(String roleName) {
        validate();

        pushMessage("Clearing role " + roleName + " to " + getOID());
        getDataStore().doRoleClear(m_type, roleName, m_data);
        popMessage();
    }

    /**
     * This method is called in order to retrieve an object from a role where
     * that role has a multiplicity with an upper bound of one or less. This
     * method populates <i>dc</i> with the state of the object being
     * retrieved.
     *
     * @param roleName The role being retrieved.
     * @param dc The empty DataContainer of the object being retrieved.
     *
     * @return True if the role was retrieved, false otherwise.
     **/

    protected boolean doRoleRetrieve(String roleName, DataContainer dc)
        throws PersistenceException {
        validate();

        // DataStore uses static metadata and connection pooling to
        // get actual database connection and meta type information
        pushMessage("Retrieving information for role " + roleName +
                    " for DataContainer; OID " + getOID());
        boolean toReturn =
            getDataStore().doRoleRetrieve(m_type, roleName, m_data, dc);
        popMessage();
        return toReturn;
    }

    /**
     * Checks that the passed in property is not null and if it is generates
     * an exception.
     * 
     * @param name The name of the property to check.
     * @exception PersistenceException Thrown if the object type does
     * not have the specified property
     *
     * @author <a href="mailto:mbryzek@arsdigita.com">Michael Bryzek</a>
     **/
    private Property checkProperty(String name) 
        throws PersistenceException
    {
        Property prop = m_type.getProperty(name);

        if (prop == null) {
            // Append all the supertypes to the error message so that
            // the user understands where all the properties are
            // defined.
            StringBuffer sb = new StringBuffer();
            sb.append("Object type \"")
                .append(m_type.getQualifiedName())
                .append("\" does not contain the property \"")
                .append(name)
                .append("\"");

            ObjectType type = m_type.getSupertype();
            if (type != null) {
                sb.append("This property also does not belong ")
                    .append("to any of its supertypes: ");
                while(type != null) {
                    sb.append(type.getQualifiedName());
                    type = type.getSupertype();
                    if (type != null) {
                        sb.append(", ");
                    }
                }
            }

            throw new PersistenceException(sb.toString());
        }

        return prop;
    }


    /**
     * Pushes a message onto the stack for the current session.
     **/
    private void pushMessage(String message) {
        SessionManager.getSession().pushMessage(message);
    }


    /**
     * Pops a message from the stack for the current session.
     **/
    private void popMessage() {
        SessionManager.getSession().popMessage();
    }


    /**
     * Is this data object equal to another data object?
     * Default implementation only compares OID (provided 
     * it isn't empty).
     *
     * @param category A category
     * @return true if the two objects have the same OID, 
     *  unless no OID info exists in which case true if they
     *  are at the same memory location (the default .equals).
     */
    public boolean equals(Object object) {
        validate();

        if (object instanceof DataObject) {
            OID id1 = this.getOID();
            // we only want to match based on OID if the OID is non-empty.
            if (! id1.arePropertiesNull()) {
                return id1.equals(((DataObject)object).getOID());
            } else {
                return super.equals(object);
            }
        }
        return false;
    }


    /**     
     * We override the standard hashCode method because
     * we have overridden equals.
     *  
     * This delegates to OID.hashCode, unless the OID is
     * empty, in which case we delegate to super.hashCode
     * and end up with something based on this object's 
     * location in memory.
     */
    public int hashCode() {
        validate();

        OID id1 = this.getOID();
        if (! id1.arePropertiesNull()) {
            return id1.hashCode();
        } else {
            return super.hashCode();
        }
    }


    /**
     * Checks that the object is in a valid state for reading.
     **/

    void validate() {
        validate(false);
    }


    /**
     * Checks that the object is in a valid state for reading and writing.
     **/

    void validate(boolean write) {
        if (!m_valid) {
            throw new PersistenceException(
                "This data object is not valid. This usually results " +
                "from using a data object that was retrieved during " +
                "a transaction that has been rolled back."
                );
        }

        if (write && m_disconnected) {
            throw new PersistenceException(
                "Cannot write to the database using a disconnected object."
                );
        }

        if (!m_disconnected && m_session != SessionManager.getSession()) {
            throw new PersistenceException(
                "This data object is being accessed from another " +
                "thread before its originating transaction has terminated."
                );
        }
    }

    /**
     * Disconnects this DataObject from the current transaction. This allows
     * the data object to be used in multiple transactions.
     *
     * @see isDisconnected()
     **/

    public void disconnect() {
        disconnect(true);
    }

    /**
     * Disconnects the data object from the current session.
     *
     *  @pre getSession() != null
     *
     *  @post isDisconnected()
     **/

    void disconnect(boolean valid) {
        Assert.assertTrue(
            getSession() != null,
            "Session is null"
            );

        if (isDisconnected()) {
            return;
        }

        if (isModified()) {
            valid = false;
        }

        m_valid = valid;
        m_disconnected = true;

        // This will clear out any data objects held onto by associations.
        m_data.sync();

        for (Iterator it = m_data.getDataAssociations(); it.hasNext(); ) {
            DataAssociationImpl da = (DataAssociationImpl) it.next();
            if (da != null) {
                da.sync();
            }
        }
    }


    /**
     * This is used to reconnect the data object to the current session if
     * necessary.
     **/

/*    private synchronized void reconnect() {
        if (m_session == null) {
            setSession(SessionManager.getSession());

            if (isNew()) { return; }

            for (Iterator it = m_type.getProperties(); it.hasNext(); ) {
                Property prop = (Property) it.next();
                if (prop.isRole() && !prop.isCollection() &&
                    !m_data.isPropertyModified(prop.getName())) {
                    m_data.clearProperty(prop.getName());
                }
            }

            // Refetch to be sure there is no stale data.
            if (!doRetrieve(getOID())) {
                m_isDeleted = true;
                throw new PersistenceException(
                    "This data object has been deleted."
                    );
            }
        } else if (m_session != SessionManager.getSession()) {
            throw new PersistenceException(
                "This data object is currently being used in another thread."
                );
        }
    }
*/

    public GenericDataObject copy() {
        GenericDataObject result =
            GenericDataObjectFactory.createObject(m_type, m_session, m_isNew);
        result.m_data = m_data.copy();
        return result;
    }

}
