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

import java.sql.Connection;

/**
 * <p>All persistence operations take place within the context of a session.
 * The operational persistence methods operate on the object types and
 * associations defined in the Persistence Definition Langauge (PDL) files.
 * The Session object has the operational methods for creating and
 * retrieving data objects.  The APIs that operate on the PDL-defined
 * metadata are in the {@link com.arsdigita.persistence.metadata} package.
 * The Session object can be retrieved from the static
 * {@link com.arsdigita.persistence.SessionManager#getSession()} method.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2002/10/16 $
 * @see com.arsdigita.persistence.SessionManager
 */
public interface Session {

   /**
     *  - Sets the connection
     * info for the specified schema.
     *
     * @param schema The name of the schema.
     * @param url The JDBC URL.
     * @param username The db username.
     * @param password The db password.
     **/
    void setSchemaConnectionInfo(String schema, String url,
                                        String username, String password);

    /**
     * Retrieves the {@link TransactionContext}
     * associated with this Session. Every Session has exactly one
     * TransactionContext object.  The transaction context can be
     * obtained as in this example:
     *   <pre>
     *   Session ssn = SessionManager.getSession();
     *   TransactionContext txn = ssn.getTransactionContext();
     *   </pre>
     *
     * The TransactionContext can be used to:
     *
     * <ul>
     *   <li>Begin a transaction.
     *     <pre>
     *     txn.beginTxn();
     *     </pre>
     *   <li>Commit a transaction.
     *     <pre>
     *     txn.commitTxn();
     *     </pre>
     *   <li>Abort a transaction.
     *     <pre>
     *     txn.abortTxn();
     *     </pre>
     *   <li>Check whether a transaction is currently in progress
     *     <pre>
     *     if (txn.inTxn()) {
     *         System.out.println("Currently in a transaction.");
     *     }
     *     </pre>
     *   <li>Check the isolation level of the current transaction.
     *     <pre>
     *     if (txn.getTransactionIsolation() ==
     *         java.sql.Connection.TRANSACTION_NONE) {
     *         System.err.println("Transaction isolation level is too low.");
     *     }
     *     </pre>
     *   <li>Set the isolation level of the current transaction.
     *     <pre>
     *  txn.setTransactionIsolation
     *     (java.sql.Connection.TRANSACTION_READ_UNCOMMITTED);
     *     </pre>
     * </ul>
     *
     * @see SessionManager
     * @see java.sql.Connection
     *
     * @return The transaction context for this Session.
     **/
    TransactionContext getTransactionContext();

    /**
     * Returns the JDBC connection associated with this session.
     *
     * @return The JDBC connection used by this Session object.
     **/
    Connection getConnection();

    /**
     * Creates and returns a DataObject of the given type. All fields of
     * this object are initially set to null, and it is not persisted until
     * {@link DataObject#save()}
     * is called.<P>
     * Because of the initial null values, this method should only be
     * used for creating new objects.  GenericDataObjectFactory.createObject
     * is suitable for creating objects that will then be populated with
     * information from the database (e.g. objects that are being retrieved
     * rather than created as new).
     *
     * @param type The type of the object to be created.
     * @return A persistent object of the specified type.
     * @see #create(String)
     * @see GenericDataObjectFactory
     */
    DataObject create(ObjectType type);

    /**
     * Creates and returns an empty DataObject of the given type. The
     * properties in the data object may then be initialized using
     * {@link DataObject#set(String,Object)}.
     * Once this is done the object may be persisted using
     * {@link DataObject#save()}.
     * An example:
     *
     *   <pre>
     *   Session ssn = SessionManager.getSession();
     *   DataObject employee = ssn.create("com.dotcom.Employee");
     *
     *   employee.set("name", "John Doughnut");
     *   employee.set("id", new BigInteger(12345));
     *   employee.set("title", "Developer");
     *
     *   employee.save();
     *   </pre>
     *
     * @see SessionManager
     *
     * @param typeName The qualified name of the type of object to be
     * created.
     *
     * @return A persistent object of the type identified by
     * <i>typeName</i>.
     **/
    DataObject create(String typeName)
        throws PersistenceException;

    /**
     * Creates a new DataObject with the type of the given oid and initializes
     * the key properties to the values specified in the oid.
     *
     * @param oid The OID that specifies the type of and key properties for
     *        the resulting DataObject.
     **/

    DataObject create(OID oid);

    /**
     * Retrieves the DataObject specified by <i>oid</i>.  If there is
     * no object of the given type with the given OID, then null is returned.
     * The retrieval will be executed with the persistence mechanism
     * assoicated with this session.  Null is also returned if any
     * of the statements in the "Retrieve" event for the data object failed.
     *
     * @param oid The id of the object to be retrieved.
     *
     * @return A persistent object of the type specified by the oid.
     **/
    DataObject retrieve(OID oid)
        throws PersistenceException;

    /**
     *  - Deletes the
     * persistent object of the given type with the given oid.  This method
     * is not yet implemented.
     *
     * @param oid The id of the object to be deleted.
     *
     * @return True of an object was deleted, false otherwise.
     **/
    boolean delete(OID oid);

    /**
     * Retrieves a collection of objects of the specified objectType.
     * This method executes the <code>retrieveAll</code> event defined
     * in the PDL and then returns a DataCollection.  This data collection
     * can be filtered and iterated over to retrieve data for the object.
     *
     * @param type The type of the persistent collection.
     * @return A DataCollection of the specified type.
     * @see Session#retrieve(String)
     **/
    DataCollection retrieve(ObjectType type);

    /**
     * <p>Retrieves a collection of objects of the specified objectType.
     * This method executes the <code>retrieveAll</code> event defined
     * in the PDL and then returns a DataCollection.  This data collection
     * can be filtered and iterated over to retrieve data for the object.
     * </p>
     * The <code>retrieveAll</code> event can be defined as in this
     * example:
     * <pre>
     * retrieveAll {
     *   do {
     *     select *
     *     from users
     *   } map {
     *     firstName=users.first_name;
     *     lastName=users.last_name;
     *   }
     * }
     * </pre>
     *
     * From Java, you can retrieve all of the users as a DataCollection,
     * and add filters.
     *
     * <pre>
     * DataCollection allUsers = session.retrieve("users");
     * allUsers.addEqualsFilter("firstName", "Smith")
     * while (allUsers.next()) {
     *   System.out.println(allUsers.get("firstName") +
     *     allUsers.get("lastName") +
     *     allUsers.get("groupName"));
     * }
     * </pre>
     * It is also possible to instantiate a data object from a DataCollection,
     * using {@link DataCollection#getDataObject()}.
     *
     * @param typeName The qualified name of the type of the object to be
     * created.
     * @return A DataCollection populated by the specified object type's
     * <code>retrieveAll</code> event..
     * @see Session#retrieve(ObjectType)
     **/
    DataCollection retrieve(String typeName)
        throws PersistenceException;

    /**
     * <p>Retrieves a persistent query object based on the named query.
     * The query must be defined with the specified name in the
     * the PDL.</p>
     * <p>
     * DataQuery objects can be used to access fields from several data
     * objects (representing columns in separate database tables) in a
     * lightweight fashion.  The example belows show you can use a DataQuery
     * to access information about users and groups.</p>
     *
     * <pre>
     * query UsersGroups {
     * do {
     *   select *
     *   from users, groups, membership
     *   where users.user_id = membership.member_id
     *   and membership.group_id = groups.group_id
     * } map {
     *   firstName=users.first_name;
     *   lastName=users.last_name;
     *   groupName=groups.group_name;
     * }
     * </pre>
     *
     * You can use this query and filter it further. Let's say I wanted to
     * get all users whose first name is "Smith":
     *
     * <pre>
     * DataQuery query = session.retrieveQuery("UsersGroups");
     * query.addEqualsFilter("firstName", "Smith")
     * while (query.next()) {
     * System.out.println(query.get("firstName") + query.get("lastName") +
     *   query.get("groupName"));
     * }
     * </pre>
     *
     * The filter will add the necessary "where" clause to the SQL.  The
     * DataQuery can then be iterated over to display the appropriate data.
     *
     * @param name The name of the query.
     * @return A new DataQuery object.
     */
    DataQuery retrieveQuery(String name) throws PersistenceException;

    /**
     * <p>
     * Retrieves a DML data operation based on the named query.
     * A DataOperation is used to perform an operation on the data,
     * such as a delete or an update.  The example belows
     * shows how it can be used to delete a set
     * of categories.</p>
     *
     *<pre>
     * data operation deleteCategories {
     *   delete from cat_categories
     *   where enabled_p = 0
     * }
     *</pre>
     * <p>
     * The data operation defined in the SQL is accessed with the
     * {@link DataOperation} object.
     *</p>
     * <pre>
     * Sessions session = SessionManager.getSession();
     * DataOperation dop = session.retreiveDataOperation("deleteCategories");
     * dop.execute();
     * </pre>
     *
     * @param name The name of the data operation defined in the PDL.
     *
     * @return A DataOperation object corresponding to the definition
     * in the PDL.
     *
     */
    DataOperation retrieveDataOperation(String name)
        throws PersistenceException;

    /**
     *   - This retrieves the
     *  factory that is used to create the filters for this DataQuery.
     */
    FilterFactory getFilterFactory();


    /**
     *  - This allows
     * developers to push messages on to the stack.  When a PersistenceError
     * is created, it automatically reads all of the messages off of the
     * stack and prints them as part of the error message.  Every call to
     * <code>pushMessage</code> should have a corresponding call to
     * {@link Session#popMessage()}.
     *  <p>
     *  For instance, when saving an object in GenericDataObject,
     *  the code could look something like
     *  <pre>
     *  <code>
     *  public void save() {
     *  session.pushMessage("Saving object " + getOID());
     *  &lt;do the save stuff here&gt;
     *  session.popMessage();
     *  </code>
     *  </pre>
     *
     *  @param message The message to push on to the stack
     *  @see Session#getStackTrace
     */
    void pushMessage(String message);

    /**
     *  - This allows developers
     *  to pop message off of the stack.  This
     *  should be used after an action has been completed successfully
     *  and should have a corresponding call to
     * {@link Session#pushMessage(String message)}.
     *  <p>
     *  For instance, when saving an object in GenericDataObject,
     *  the code could look something like
     *  <pre>
     *  <code>
     *  public void save() {
     *  session.pushMessage("Saving object " + getOID());
     *  &lt;do the save stuff here&gt;
     *  session.popMessage();
     *  </code>
     *  </pre>
     *
     *  @return This returns the message that was popped off of the stack
     *          If the stack was empty then "null" is returned.
     *  @see Session
     */
    String popMessage();

    /**
     *   - Returns the
     *  stack trace for this session.
     *  @return This returns a String that represents all of the items
     *  that are on the stack.  It returns "Occured while:" followed
     *  by the list of items.  If the stack is empty, this returns the
     *  empty string.  Calling this clears the stack.
     *
     */
    String getStackTrace();}
