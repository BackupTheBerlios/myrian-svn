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

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Property;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

// imports for deprecated DataAssociation

// imports for deprecated DataQuery



/**
 * DataAssociationImpl - This is the default implementation for
 * DataAssociation.  This is used to represent the relationship between
 * two objects.   That is, this is used to say that two objects are
 * associated.  {@link com.arsdigita.persistence.Link} represents the
 * actual link between two objects and holds the extra information
 * about the association.
 *
 * <p>
 *
 * It is important to note that when the deprecated methods in this class
 * are removed, <font color="red"><b>this class will no long extend
 * DataCollection</b></font>.  If you want something that extends
 * DataCollection, use DataAssociationCursor instead.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #12 $ $Date: 2003/03/07 $
 */

class DataAssociationImpl extends DataCollectionImpl implements DataAssociation {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataAssociationImpl.java#12 $ by $Author: jorris $, $DateTime: 2003/03/07 01:57:54 $";

    private static final Logger s_cat =
        Logger.getLogger(DataAssociationImpl.class);


    private Set m_toAdd = new HashSet();
    private Set m_toRemove = new HashSet();
    private List m_fetched = new ArrayList();
    private boolean m_cleared = false;

    private GenericDataObject m_parent;
    private String       m_role;
    private ObjectType   m_linkType;

    // variable for deprecated DataCollectionImpl methods
    private ObjectType m_type;

    // helper variable for implementation of deprecated methods
    private DataAssociationCursorImpl m_daCursor = null;

    /**
     *  Create a new DataAssociationImpl
     *
     *  @param parent The parent GenericDataObject for the Association
     *  @param role The role that is being created
     */
    DataAssociationImpl(GenericDataObject parent, String role, Operation op) {
        super((ObjectType)parent.getObjectType().getProperty(role).getType(),
              op);
        m_type =
            (ObjectType) parent.getObjectType().getProperty(role).getType();

        // keep the below when removing methods.  The two calls above are from
        // DataQuery and DataCollection
        m_parent = parent;
        m_role = role;
        m_linkType =
            (ObjectType)
            m_parent.getObjectType().getProperty(m_role).getLinkType();
    }

    private DataAssociationCursorImpl getInternalCursor() {
        if (m_daCursor == null) {
            m_daCursor =  (DataAssociationCursorImpl)
                getDataAssociationCursor();
        }

        return m_daCursor;
    }

    /**
     *  Syncs the association with what is in the system.
     */
    void sync() {
        m_toAdd.clear();
        m_toRemove.clear();
        m_fetched.clear();
        m_cleared = false;
    }


    /**
     *  Returns the DataCollection that is created by retrieving
     *  all data objects in the association
     *
     *  @return a DataCollection that is created by retrieving
     *  all data objects in the association
     */
    public DataCollection getDataCollection() {
        return getDataAssociationCursor();
    }


    /**
     * Returns a data association iterator that allows users to iterate
     * through all of the data associations
     **/
    public DataAssociationCursor getDataAssociationCursor() {
        DataAssociationCursorImpl cursor =
            new DataAssociationCursorImpl(m_parent, m_role, this,
                                          getOperation());
        return cursor;
    }


    /**
     * Returns a data association iterator that allows users to iterate
     * through all of the data associations
     *
     * This is a convenience method for getDataAssociationCursor()
     */
    public DataAssociationCursor cursor() {
        return getDataAssociationCursor();
    }


    /**
     *  This removes all of the Associations with the parent object
     *  (It is like deleting all rows in a mapping table or updating
     *   the single linked column to null)
     *
     *  <b>This feature is not yet implemented</b>
     */
    public void clear() {
        m_toAdd.clear();
        m_cleared = true;

        m_parent.fireObserver(new ClearEvent(m_parent, m_role));
    }


    /**
     *  This returns the Link that represents the actual association between
     *  the two objects within the association
     * @deprecated Use {@link #cursor().getLink}
     */
    public Link getLink() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    /**
     *  This returns the actual link between the two object in the
     *  association.
     *
     *  @param object The object to get the link for (instead of just
     *                returning the link for the current DataObject).
     */
    public Link getLink(DataObject object) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }


    /**
     *  This returns the Link Property specified by the passed in parameter.
     *  For instance, if there is a sortKey specifying how to sort
     *  the association, calling getLinkProperty("sortKey") would return
     *  the sortKey for the given Association.
     *
     *  @param name The name of the Link Property to return
     *  @return The Link Property specified by the parameter
     * @deprecated Use {@link #cursor().getLinkProperty}
     */
    public Object getLinkProperty(String name) {
        return getInternalCursor().getLinkProperty(name);
    }


    /**
     *  This marks the passed in DataObject as an object to be added
     *
     *  @param object The object to be added to the Association
     */
    public DataObject add(DataObject object) {
        if (object == null) {
            throw new IllegalArgumentException(
                                               "Null object was passed to DataAssociation.add()"
                                               );
        }

        m_parent.fireObserver(new AddEvent(m_parent, m_role, object));

        DataObject link = null;
        if (m_linkType != null) {
            Property prop = m_parent.getObjectType().getProperty(m_role);
            Property ass = prop.getAssociatedProperty();
            link = SessionManager.getSession().create(m_linkType);
            link.set(prop.getName(), object);
            link.set(ass.getName(), m_parent);

            if (!ass.isCollection()) {
                object.set(ass.getName(), m_parent);
            }

            object = link;
        }

        if (m_toRemove.contains(object)) {
            m_toRemove.remove(object);
        } else {
            m_toAdd.add(object);
        }

        return link;
    }


    /**
     *  Removes the object associated with the current position in the
     *  collection.
     * @deprecated Use {@link #remove(DataObject object)} or
     * {@link #cursor()}, loop through the objects
     * and then call remove()
     */
    public void remove() {
        getInternalCursor().remove();
    }


    /**
     *  Removes <i>object</i> from the collection.
     *
     *  @param object The DataObject to be removed
     */
    public void remove(DataObject object) {

        m_parent.fireObserver(new RemoveEvent(m_parent, m_role, object));

        if (m_linkType != null) {
            DataObject link = GenericDataObjectFactory.createObject(
                                                                    m_linkType,
                                                                    SessionManager.getSession(),
                                                                    false
                                                                    );

            DataContainer linkData =
                ((GenericDataObject) link).getDataContainer();
            Property prop = m_parent.getObjectType().getProperty(m_role);
            Property ass = prop.getAssociatedProperty();

            linkData.initProperty(prop.getName(), object);
            linkData.initProperty(ass.getName(), m_parent);

            object = link;
        }

        if (m_toAdd.contains(object)) {
            m_toAdd.remove(object);
        } else {
            m_toRemove.add(object);
        }
    }


    /**
     * Removes <i>object</i> from the collection.
     *
     * Note: The object does NOT truly get removed from the association until
     * save() is called on the association's parent object. This means, for
     * example, that cursor() will return the same cursor that it did before
     * any objects are added.
     * @param oid The OID of the object to remove.
     */
    public void remove(OID oid) {
        remove(getObjectFromOID(oid));
    }

    void addFetched(DataObject object) {
        if (!m_parent.isDisconnected()) {
            m_fetched.add(object);
        }
    }

    /**
     * Saves all the links in this association.
     **/

    void save() {
        Property prop = m_parent.getObjectType().getProperty(m_role);
        Property ass = prop.getAssociatedProperty();

        if (m_cleared) {
            m_parent.doRoleClear(m_role);
        } else {
            for (Iterator it = m_fetched.iterator(); it.hasNext(); ) {
                GenericDataObject toSave = (GenericDataObject) it.next();
                if (!toSave.isDeleted() && toSave.isModified()) {
                    toSave.save();
                }
            }

            for (Iterator it = m_toRemove.iterator(); it.hasNext();) {
                GenericDataObject toRemove = (GenericDataObject) it.next();
                m_parent.doRoleRemove(m_role, toRemove.getDataContainer());
                if (prop.isComponent() || m_linkType != null) {
                    toRemove.delete();
                }

                if (!prop.isComponent() && ass != null &&
                    !ass.isCollection()) {
                    GenericDataObject obj =
                        (GenericDataObject) toRemove.get(prop.getName());
                    obj.getDataContainer().clearProperty(ass.getName());
                    obj.getDataContainer().initProperty(ass.getName(), null);
                }
            }
        }

        for (Iterator it = m_toAdd.iterator(); it.hasNext(); ) {
            GenericDataObject toAdd = (GenericDataObject) it.next();

            if (ass != null && !ass.isCollection()) {
                GenericDataObject obj =
                    (GenericDataObject) toAdd.get(prop.getName());
                obj.getDataContainer().clearProperty(ass.getName());
                obj.getDataContainer().initProperty(ass.getName(), m_parent);
            }

            if (prop.isComponent() || m_linkType != null) {
                toAdd.save();
            }
            m_parent.doRoleAdd(m_role, toAdd.getDataContainer());
        }

        sync();
    }


    /**
     * Deletes the contents of this association.
     **/

    void delete() {
        // Right now we do nothing because we expect object delete events or
        // on delete cascade to take care of this.
    }


    /**
     *  Returns a boolean indicating whetherr or not this association has
     *  been modified
     *
     *  @return a boolean indicating whetherr or not this association has
     *  been modified
     */
    public boolean isModified() {
        return m_toAdd.size() > 0 || m_toRemove.size() > 0;
    }


    /**
     *  This is a simple string representation of DataAssociationImpl.
     *  For something more involved, you should use
     *  {@link #toString(Set s, int level)}
     *
     *  This is not complex because if it were, it could be possible
     *  to create an infinite loop
     */
    public String toString() {
        return "DataAssociationImpl: " + Utilities.LINE_BREAK +
            "  + Parent = " + m_parent.getObjectType() + Utilities.LINE_BREAK +
            "  + Role = " + m_role;
    }


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    public void setParameter(String parameterName, Object value) {
        getInternalCursor().setParameter(parameterName, value);
    }


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName) {
        return getInternalCursor().getParameter(parameterName);
    }

    /*
     * Place an empty finalize() method here to prevent
     * persistence error where garbage collector thread
     * calls finalize() and leads to an attempt to access
     * a data object that it shouldn't
     */
    protected void finalize() throws Throwable {
    }

    /**
     * Explicitly closes this DataQuery.
     * Query will automatically be closed when next
     * returns false, but this method should be
     * explicitly called in the case where all of the data in a query
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     */
    public void close() {
        getInternalCursor().close();
    }


    /**
     * Rewinds the data query to the beginning, i.e. it's as if next() was
     * never called.
     **/
    public void rewind() {
        getInternalCursor().rewind();
    }


    /**
     * Returns the data query to its initial state by rewinding it and
     * clearing any filters or ordering.
     **/
    public void reset() {
        getInternalCursor().reset();
    }


    /**
     * Moves the cursor to the first row in the query.
     *
     * @return True if the cursor is on a valid row, false if there are no
     *         rows in the query.
     **/
    public boolean first() throws PersistenceException {
        return getInternalCursor().first();
    }


    /**
     * Returns the value of the <i>name</i> property associated with the
     * current position in the query.
     *
     * @param propertyName The name of the property.
     *
     * @return The value of the property.
     **/
    public Object get(String propertyName) {
        return getInternalCursor().get(propertyName);
    }


    /**
     * Returns the current position within the query. The first position is 1.
     *
     * @return The current position, 0 if there is none.
     **/
    public int getPosition() throws PersistenceException {
        return getInternalCursor().getPosition();
    }


    /**
     * Returns true if the query has no rows.
     *
     * @return True if the query has no rows.
     **/
    public boolean isEmpty() throws PersistenceException {
        return getInternalCursor().isEmpty();
    }


    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return True if the cursor is on the first row, false otherwise.
     **/
    public boolean isFirst() throws PersistenceException {
        return getInternalCursor().isFirst();
    }


    /**
     * Indicates whether the cursor is on the last row of the query.
     * Note: Calling the method isLast may be expensive because the
     * JDBC driver might need to fetch ahead one row in order to
     * determine whether the current row is the last row in the result
     * set.
     * <p>
     * If the query has not yet been executed, it executes the query.
     * <p>
     * This is similar to {@link com.arsdigita.db.ResultSet#isLast()}
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on the last row, false otherwise.
     **/
    public boolean isLast() throws PersistenceException {
        return getInternalCursor().isLast();
    }


    /**
     * Moves the cursor to the last row in the query.
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on a valid row, false if there are no
     *         rows in the query.
     **/
    public boolean last() throws PersistenceException {
        return getInternalCursor().last();
    }


    /**
     * Moves to the next row in the query, returning true if more objects
     * remain, false if no rows remain.
     *
     * @return False if there are no more rows, true otherwise.
     **/
    public boolean next() throws PersistenceException {
        return getInternalCursor().next();
    }


    /**
     * Moves to the previous row in the query, returning true if there are any
     * rows preceeding the current row, false otherwise.
     *
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if there are rows preceeding the current one, false
     *         otherwise.
     **/
    public boolean previous() throws PersistenceException {
        return getInternalCursor().previous();
    }


    /**
     * Sets a filter for the contents of the data query.
     *
     * @param conditions The conditions for the filter.
     * @deprecated see #addFilter
     *
     * @return The filter.
     **/
    public Filter setFilter(String conditions) {
        return getInternalCursor().setFilter(conditions);
    }


    /**
     * Adds the conditions to the filter that will be used on this
     * query.  If a filter already exists, this alters the filter
     * object and returns the altered object.  If one does not
     * already exist, it creates a new filter.  When adding
     * filters, the user should not use the same parameter name
     * in multiple filters.  That is, the following will not work
     *
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(3));
     * filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(8));
     * </code>
     * </pre>
     * The above actually evaluates to
     * <code>"priority < 3 and priority > 3"</code>
     * which is clearly now what the developer wants.
     * <p>
     * The following will work.
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :lowerBound");
     * filter.set("lowerBound", new Integer(3));
     * filter = query.addFilter("priority < :upperBound");
     * filter.set("upperBound", new Integer(8));
     * </code>
     * </pre>
     * It is actually the same as
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :lowerBound
     *                                  and priority > :uperBound");
     * filter.set("upperBound", new Integer(8));
     * filter.set("lowerBound", new Integer(3));
     * </code>
     * </pre>
     *
     * @param conditions The conditions for the filter.  This is a string
     *        that should represent part of a SQL "where" clause.  Specifically,
     *        it should normally take the form of
     *        <pre><code>
     *        &lt;column_name&gt; &lt;condition&gt; &lt;attribute bind variable&gt;
     *        </code></pre>
     *        where the "condition" is something like "=", "&lt;", "&gt;", or
     *        "!=".  The "bind variable" should be a colon followed by
     *        some attribute name that will later be set with a call to
     *        {@link com.arsdigita.persistence.Filter#set(java.lang.String,
     *               java.lang.Object)}
     *        <p>
     *        It is possible to set multiple conditions with a single
     *        addFilter statement by combining the conditions with an "and"
     *        or an "or".  Conditions may be grouped by using parentheses.
     *        Consecutive calls to addFilter append the filters using
     *        "and".
     *
     *        <p>
     *        If there is already a filter that exists for this query
     *        then the passed in conditions are added to the current
     *        conditions with an AND like <code>(&lt;current conditions&gt;)
     *        and (&lt; passed in conditions&gt;)</code>
     *
     * @return The filter that has just been added to the query
     **/
    public Filter addFilter(String conditions) {
        return getInternalCursor().addFilter(conditions);
    }


    /**
     *  This adds the passed in filter to this query and ANDs it with
     *  an existing filters.  It returns the filter for this query.
     */
    public Filter addFilter(Filter filter) {
        return getInternalCursor().addFilter(filter);
    }


    /**
     * Highly experimental, for use by permissions service only.
     */
    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return getInternalCursor().addInSubqueryFilter(propertyName, subqueryName);
    }

    /**
     * Clears the current filter for the data query.
     **/
    public void clearFilter() {
        getInternalCursor().clearFilter();
    }


    /**
     *  This retrieves the factory that is used to create the filters
     *  for this DataQuery
     */
    public FilterFactory getFilterFactory() {
        return getInternalCursor().getFilterFactory();
    }


    /**
     * Set the order in which the query will be returned.
     *
     * @deprecated see #addOrder
     * @param order The order.
     **/
    public void setOrder(String order) throws PersistenceException {
        getInternalCursor().setOrder(order);
    }


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.addOrder("creationDate desc, id");
     * </pre></blockquote>
     *
     * @param order This String parameter specifies the ordering of the
     *              output.  This should be a comma seperated list
     *              of Attribute names (not the database column names)
     *              in the order of precedence.
     *              Separating attributes by commas is the same as
     *              calling addOrder multiple times, each with the
     *              next attribute.  For instance, this
     *              <pre><code>
     *              addOrder("creationDate");
     *              addOrder("creationUser");
     *              </code></pre>
     *              is the same as
     *              <pre><code>
     *              addOrder("creationDate, creationUser");
     *              </code></pre>
     *
     *              <p>
     *              If the items should be ordered in ascending order,
     *              the attribute name should be followed by the word "asc"
     *              If the items should be ordered in descending order,
     *              the attribute should be followed by the word "desc"
     *              For instance, or order by ascending date and descending
     *              user (for users created with the same date), you would
     *              use the following:
     *              <pre><code>
     *              addOrder("creationDate asc, creationUser desc");
     *              </code></pre>
     *
     **/
    public void addOrder(String order) throws PersistenceException {
        getInternalCursor().addOrder(order);
    }


    /**
     * Clears the current order for the data query.
     **/
    public void clearOrder() {
        getInternalCursor().clearOrder();
    }


    private DataContainer m_data;

    void setDataContainer(DataContainer data) {
        m_data = data;
    }

    DataContainer getDataContainer() {
        return m_data;
    }

    /**
     * Returns the size of this query (i.e. the number of rows that
     * are returned). This method wraps the current sql query in a
     * <pre>select count(*) from (...)</pre>. In the future, order by
     * clauses should be removed for more efficiency.
     **/
    public long size() throws PersistenceException {
        return getInternalCursor().size();
    }


    /**
     *  Deprecated methods from DataCollection
     */

    /**
     *  This returns the next DataObject in the "collection"
     */
    public DataObject getDataObject() {
        return getInternalCursor().getDataObject();
    }


    /**
     *  This returns the ObjectType for the DataObjects returned by
     *  this "collection"
     */
    public ObjectType getObjectType() {
        return getInternalCursor().getObjectType();
    }


    /**
     *  This takes and OID and creates the corresponding DataObject
     *  without hitting the database and with setting all of the
     *  values that have been specified by the passed in OID
     *
     *  @param oid The OID to use to create the DataObject
     */
    private DataObject getObjectFromOID(OID oid) {
        ObjectType objectType = oid.getObjectType();
        GenericDataObject object =
            GenericDataObjectFactory.createObject(
                                                  objectType,
                                                  SessionManager.getSession(),
                                                  false
                                                  );

        // now we set all of the properties specified by the OID
        Iterator iterator = objectType.getKeyProperties();
        while (iterator.hasNext()) {
            String propertyName = ((Property)iterator.next()).getName();
            Object value = oid.get(propertyName);
            if (value != null) {
                object.getDataContainer().initProperty(propertyName, value);
            }
        }
        return object;
    }

    public boolean contains(OID oid) {
        return cursor().contains(oid);
    }

    public boolean contains(DataObject dobj) {
        return cursor().contains(dobj);
    }

}
