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

import com.arsdigita.persistence.metadata.Association;
import com.arsdigita.persistence.metadata.DataType;
import com.arsdigita.persistence.metadata.CompoundType;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.QueryType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.metadata.Column;
import com.arsdigita.persistence.metadata.SimpleType;
import com.arsdigita.persistence.metadata.DataOperationType;
import com.arsdigita.persistence.metadata.Event;
import com.arsdigita.persistence.metadata.Operation;
import com.arsdigita.persistence.metadata.Mapping;

import com.arsdigita.persistence.sql.SQLWriter;
import com.arsdigita.persistence.sql.Identifier;
import com.arsdigita.persistence.sql.Element;
import com.arsdigita.persistence.sql.Statement;
import com.arsdigita.persistence.sql.SetClause;
import com.arsdigita.persistence.sql.Clause;
import com.arsdigita.persistence.sql.ParseException;
import com.arsdigita.persistence.sql.Assign;

import com.arsdigita.db.CallableStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import java.io.StringReader;
import java.io.Writer;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Title:       DataStore class
 * Description: This class encapsulates all database specific logic for the
 *              storage and retrieval of objects. This class may delegate to
 *              other classes that are responsible for specifics such as
 *              SQL statement construction and our Database API for
 *              communicating with the database.
 * Copyright:    Copyright (c) 2001
 * Company:      ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2002/08/15 $
 */

public class DataStore {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataStore.java#10 $ by $Author: jorris $, $DateTime: 2002/08/15 14:01:26 $";

    private static final Logger log =
        Logger.getLogger(DataStore.class.getName());

    private TransactionContext m_txnCtx;
    //    private EventBuilder m_builder = new StaticEventBuilder();


    /**
     *  Creates a new DataStore
     */
    public DataStore() {
        m_txnCtx = new TransactionContext();
    }


    /**
     *  Returns the transaction context for this DataStore
     *  @return the TransactionContext for this DataStore.
     */
    TransactionContext getTransactionContext() {
        return m_txnCtx;
    }


    /**
     *  This returns the connection used by the DataStore
     *  @return the Connection for the DataStore
     */
    public Connection getConnection() {
        return m_txnCtx.getConnection();
    }

    public boolean fireEvent(Event event, DataContainer source,
                             DataContainer target) {
        return fireEvent(event, source, target, false);
    }

    public boolean fireEvent(Event event, DataContainer source,
                             DataContainer target, boolean lazyUpdates) {
        boolean result = true;

        for (Iterator it = event.getOperations(); it.hasNext(); ) {
            Operation op = (Operation) it.next();
            ResultSet rs = fireOperation(op, source, lazyUpdates);
            if (rs == null) {
                continue;
            }
            try {
                if (rs.next()) {
                    populate(target, op, rs);
                } else {
                    result = false;
                }
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            } finally {
                try {
                    // associated statement closing should be handled
                    // automatically if close after use flag was set.
                    rs.close();
                } catch (SQLException ex) {
                    throw PersistenceException.newInstance(ex);
                }
            }
        }

        return result;
    }

    public void populate(DataContainer target, Operation op, ResultSet rs) {
        for (Iterator it = op.getMappings(); it.hasNext(); ) {
            Mapping mapping = (Mapping) it.next();
            String[] path = mapping.getPath();
            Property prop = target.lookupProperty(path);
            if (!prop.isAttribute()) {
                throw new PersistenceException
                    ("Path points to compound type: " +
                     DataContainer.formatPath(path));
            }
            SimpleType type = (SimpleType) prop.getType();
            try {
                String colName = mapping.getColumn();
                Object obj = type.fetch(rs, colName);
                logAssignment(path, obj, colName);
                target.initPath(path, obj);
            } catch (SQLException e) {
                throw PersistenceException.newInstance
                    ("Error while trying to fetch property " + prop +
                     " of type " + type, e);
            }
        }
    }

    private static final void logAssignment(String[] path, Object obj,
                                            String colName) {
        if (log.isInfoEnabled()) {
            log.info(DataContainer.formatPath(path) + " = " +
                     obj + " (" + colName + ")");
        }
    }

    public ResultSet fireOperation(Operation op, DataContainer source) {
        return fireOperation(op, source, false);
    }

    private Statement processUpdate(Statement stmt, DataContainer source) {
        Statement result = new Statement();
        for (Iterator it = stmt.getClauses(); it.hasNext(); ) {
            Clause clause = (Clause) it.next();
            if (clause.isSetClause()) {
                SetClause setClause =
                    processSetClause((SetClause) clause, source);
                if (setClause == null) {
                    return null;
                } else {
                    result.addClause(setClause);
                }
            } else {
                result.addClause(clause);
            }
        }

        return result;
    }

    private SetClause processSetClause(SetClause clause,
                                       DataContainer source) {
        SetClause result = new SetClause();

        for (Iterator it = clause.getAssigns(); it.hasNext(); ) {
            Assign assign = (Assign) it.next();
            List leafs = assign.getLeafElements();
            for (int i = 0; i < leafs.size(); i++) {
                Element el = (Element) leafs.get(i);
                if (el.isBindVar()) {
                    Identifier id = (Identifier) el;
                    if (keepAssign(id.getPath(), source)) {
                        result.addAssign(assign);
                        break;
                    }
                }
            }
        }

        if (result.getAssigns().hasNext()) {
            return result;
        } else {
            return null;
        }
    }

    private boolean keepAssign(String[] path, DataContainer source) {
        DataContainer dc = source;
        for (int i = 0; i < path.length; i++) {
            if (dc.isPropertyModified(path[i])) {
                return true;
            }

            Object obj = dc.get(path[i]);
            if (obj == null) {
                return false;
            }

            if (obj instanceof GenericDataObject) {
                dc = ((GenericDataObject) obj).getDataContainer();
            }
        }

        return false;
    }

    public ResultSet fireOperation(final Operation op,
                                   final DataContainer source,
                                   boolean lazyUpdates) {
        Element el = Element.parse(op.getSQL());

        logOperation(op, source, lazyUpdates);

        if (lazyUpdates && el.isUpdate()) {
            el = processUpdate((Statement) el, source);
            if (el == null) {
                return null;
            }
        }

        SQLWriter sql = new SQLWriter();

        final int size = 10;
        final List types = new ArrayList(size);
        final List jdbcTypes = new ArrayList(size);
        final List values = new ArrayList(size);
        final List refreshPaths = new ArrayList(size);

        el.output(sql, new Element.Transformer() {
                public boolean transform(Element element,
                                         SQLWriter result) {
                    if (element.isBindVar()) {
                        Identifier id = (Identifier) element;
                        Object value = source.lookupValue(id.getPath());
                        values.add(value);

                        Property prop = source.lookupProperty(id.getPath());
                        SimpleType type = (SimpleType) prop.getType();
                        types.add(type);

                        int jdbcType = jdbcType(op, id.getPath(), prop);
                        jdbcTypes.add(new Integer(jdbcType));

                        result.print(type.getLiteral(value, jdbcType));
                        if (type.needsRefresh(value, jdbcType)) {
                            refreshPaths.add(id.getPath());
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            });

        com.arsdigita.db.PreparedStatement ps;
        // This will only work correctly if conn is really of type
        // com.arsdigita.db.Connection.
        try {
            ps = (com.arsdigita.db.PreparedStatement)getConnection()
                .prepareStatement(sql.toString());
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        } catch (ClassCastException e) {
            throw PersistenceException.newInstance(e);
        }

        // we always set close after use, because we only
        // use our preparedstatement for binding, not for
        // reuse.
        ps.setCloseAfterUse(true);

        int index = 1;
        for (int i = 0; i < values.size(); i++) {
            Object value = values.get(i);
            SimpleType type = (SimpleType) types.get(i);
            int jdbcType = ((Integer) jdbcTypes.get(i)).intValue();

            try {
                index += type.bind(ps, index, value, jdbcType);
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            } catch (ClassCastException e) {
                throw PersistenceException.newInstance
                    ("Error binding value number " + index +
                     " in sql: " + sql + "\n" + e.getMessage());
            }
        }

        if ((!el.isSelect() || el.isSelectForUpdate()) && !el.isDDL()) {
            ps.setNeedsAutoCommitOff(true);
        } else {
            ps.setNeedsAutoCommitOff(false);
        }


        // TODO: Add any checks about DDL?  DEE 7/24/01

        try {
            if (ps.execute()) {
                return ps.getResultSet();
            } else {
                doRefresh(source, refreshPaths);
                return null;
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance
                ("Error executing SQL: " + Utilities.LINE_BREAK +
                 sql.toString() +
                 Utilities.LINE_BREAK + "Variables Set: " +
                 values, e);
        }
    }

    private static final void logOperation(Operation op, DataContainer source,
                                           boolean lazyUpdates) {
        if (log.isInfoEnabled()) {
            if (op.getLineNumber() < 0) {
                CompoundType type = source.getType();
                log.info(
                         "Firing operation autogenerated for " +
                         type.getQualifiedName() + " " +
                         type.getFilename() + ":" +
                         type.getLineNumber()
                         );
            } else {
                log.info("Firing operation " + op.getFilename() + ":" +
                         op.getLineNumber());
            }
        }
    }

    private static final int jdbcType(Operation op, String[] path,
                                      Property prop) {
        if (op.hasBindType(path)) {
            return op.getBindType(path);
        } else {
            Column column = prop.getColumn();
            if (column != null && column.getType() > Integer.MIN_VALUE) {
                return column.getType();
            } else {
                return ((SimpleType) prop.getType()).getJDBCtype();
            }
        }
    }


    /**
     * This method is called by fireOperation in order to complete updating or
     * inserting of a value. Certain SimpleType objects (LOBs) update values
     * by mutating them in place. This method takes a data container and a
     * list of paths that identify values that need to be updated via
     * performing a select and then passes the result set into a callback on
     * SimpleType. This callback then does the actual work of updating the
     * value. Note that even though this method does a select, it actually
     * uses the result of this select to write value from the local memory to
     * the db.
     *
     * @param source The DataContainer containing values that need to be
     *               written to the db.
     * @param paths A list of String[]s that identify properties that need to
     *              be updated in this manner.
     **/

    private void doRefresh(DataContainer source, List paths)
        throws SQLException {
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            String[] path = (String[]) it.next();
            Property prop = source.lookupProperty(path);
            SimpleType type = (SimpleType) prop.getType();
            Object value = source.lookupValue(path);
            Operation op = findRetrieveOperation(source, path);

            if (op == null) {
                throw new PersistenceException(
                                               "Unable to find an operation to refresh this property: " +
                                               source.getType().getQualifiedName() + "." +
                                               DataContainer.formatPath(path)
                                               );
            }

            ResultSet rs = fireOperation(op, source);

            if (rs.next()) {
                for (Iterator mappings = op.getMappings();
                     mappings.hasNext(); ) {
                    Mapping mapping = (Mapping) mappings.next();
                    if (mapping.getPath()[0].equals(prop.getName())) {
                        String colName = mapping.getColumn();
                        type.doRefresh(rs, colName, value);
                        rs.close();
                        break;
                    }
                }
            } else {
                throw new IllegalStateException(
                                                "Couldn't fetch value for updating."
                                                );
            }
        }
    }

    private static final Operation findRetrieveOperation(DataContainer source,
                                                         String[] path) {
        Operation result;

        Property prop = source.lookupProperty(path);
        Event event = prop.getEvent(Property.RETRIEVE);

        result = findRetrieveOperation(event, prop);

        if (result == null) {
            CompoundType type = source.getType();

            for (int i = 0; i < path.length - 1; i++) {
                type = (CompoundType) type.getProperty(path[i]).getType();
            }

            event = type.getEvent(CompoundType.RETRIEVE);
            result = findRetrieveOperation(event, prop);

            if (result == null) {
                event = type.getEvent(CompoundType.RETRIEVE_ATTRIBUTES);
                result = findRetrieveOperation(event, prop);
            }
        }

        return result;
    }

    private static final Operation findRetrieveOperation(Event event,
                                                         Property prop) {
        if (event == null) {
            return null;
        }

        for (Iterator it = event.getOperations(); it.hasNext(); ) {
            Operation op = (Operation) it.next();
            for (Iterator mappings = op.getMappings(); mappings.hasNext(); ) {
                Mapping mapping = (Mapping) mappings.next();
                if (mapping.getPath()[0].equals(prop.getName())) {
                    return op;
                }
            }
        }

        return null;
    }

    /**
     *  @param op This is the operation that is used for the cration
     *  @param source This is the DataContainer that is used to specify
     *                the values for the variables in the operation
     *  @param variables This is the List that contains the variables
     *         within the operation.  The order of this list is the order
     *         of the bind variables.  This is typically a new List object
     *         that has elements added to it by this procedure
     */
    public CallableStatement fireCallableOperation(Operation op,
                                                   DataContainer source,
                                                   List variables) {
        Element el = Element.parse(op.getSQL());

        StringBuffer sql = new StringBuffer("{");

        List leafs = el.getLeafElements();

        for (int i = 0; i < leafs.size(); i++) {
            Element leaf = (Element) leafs.get(i);
            if (leaf.isBindVar()) {
                // we need the substring call to get rid of the leading ":"
                variables.add(((Identifier) leaf).toString().substring(1));
                sql.append("?");
                if (i == 0) {
                    sql.append(" = call ");
                    i++;
                }
            } else {
                if (i == 0) {
                    sql.append("call ");
                }
                sql.append(" " + leaf + " ");
            }
        }
        sql.append("}");
        String sqlString = sql.toString();

        com.arsdigita.db.CallableStatement cs;
        // This will only work correctly if conn is really of type
        // com.arsdigita.db.Connection.
        try {
            cs = (com.arsdigita.db.CallableStatement)getConnection()
                .prepareCall(sqlString);
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        } catch (ClassCastException e) {
            throw PersistenceException.newInstance(e);
        }

        // this close after use will be automatically set to false if any
        // out params are registered, since we can't know when
        // to close if there are out parameters.
        cs.setCloseAfterUse(true);
        try {
            for (int i = 0; i < variables.size(); i++) {
                String variable = (String) variables.get(i);
                cs.registerOutParameter(i + 1, op.getBindType(variable));
                Object value = source.lookupValue(new String[] {variable});
                if (value != null) {
                    // the next IF statement allows us to allow users
                    // to pass in java.util.Date instead of having to
                    // pass in java.sql.Date (and loose the seconds) or
                    // the java.sql.Timestamp
                    if (value instanceof java.util.Date) {
                        value = new java.sql.Timestamp
                            (((java.util.Date)value).getTime());
                    }
                    cs.setObject(i + 1, value);
                }
            }
        } catch (Exception e) {
            throw PersistenceException.newInstance
                ("Error preparing CallableStatement: " + Utilities.LINE_BREAK +
                 sql.toString() +
                 Utilities.LINE_BREAK + "Variables: " +
                 variables, e);
        }

        if ((!el.isSelect() || el.isSelectForUpdate()) && !el.isDDL()) {
            cs.setNeedsAutoCommitOff(true);
        } else {
            cs.setNeedsAutoCommitOff(false);
        }

        try {
            cs.execute();
            return cs;
        } catch (SQLException e) {
            throw PersistenceException.newInstance
                ("Error executing SQL: " + Utilities.LINE_BREAK +
                 sqlString, e);
        }
    }


    private static final Event getEvent(CompoundType type, int eventType) {
        Event result = type.getEvent(eventType);

        if (result == null) {
            StringBuffer msg = new StringBuffer("No ");

            switch (eventType) {
            case CompoundType.INSERT:
                msg.append("insert");
                break;
            case CompoundType.UPDATE:
                msg.append("update");
                break;
            case CompoundType.DELETE:
                msg.append("delete");
                break;
            case CompoundType.RETRIEVE:
                msg.append("retrieve");
                break;
            case CompoundType.RETRIEVE_ALL:
                msg.append("retrieve all");
                break;
            case CompoundType.RETRIEVE_ATTRIBUTES:
                msg.append("retrieve attributes");
                break;
            default:
                throw new IllegalStateException(
                                                "Unknown event type: " + eventType
                                                );
            }

            msg.append(" event defined for " +
                       type.getQualifiedName() + ".");

            throw new UndefinedEventException(msg.toString());
        }

        return result;
    }

    private static final Event getEvent(CompoundType type, String propName,
                                        int eventType) {
        return getEvent(type, type.getProperty(propName), eventType);
    }

    private static final Event getEvent(CompoundType type, Property prop,
                                        int eventType) {
        Event result = prop.getEvent(eventType);

        if (result == null) {
            StringBuffer msg = new StringBuffer("No ");

            switch (eventType) {
            case Property.ADD:
                msg.append("add");
                break;
            case Property.REMOVE:
                msg.append("remove");
                break;
            case Property.RETRIEVE:
                msg.append("retrieve");
                break;
            case Property.CLEAR:
                msg.append("clear");
                break;
            default:
                throw new IllegalStateException(
                                                "Unknown event type: " + eventType
                                                );
            }

            msg.append(" " + prop.getName() + " defined for " +
                       type.getQualifiedName() + ".");

            throw new UndefinedEventException(msg.toString());
        }

        return result;
    }


    /**
     *
     */
    public boolean doRetrieve(ObjectType type, OID oid, DataContainer dc)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to retrieve object data and
        // populate data container

        for (Iterator it = type.getKeyProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            String name = prop.getName();
            if (oid.hasProperty(name)) {
                dc.initProperty(name, oid.get(name));
            } else {
                throw new PersistenceException(
                                               "OID doesn't match object key: " + oid
                                               );
            }
        }

        return fireEvent(getEvent(type, CompoundType.RETRIEVE), dc, dc);
    }


    /**
     *  This obtains the insert event for the passed in type,
     *  uses the DataContainer for the bind information, and
     *  the performs the insert
     *
     *  @param type The ObjectType to use to look up the insert statement
     *  @param dc The DataContainer to use to bind the variables in the
     *            insert statements
     */
    public void doInsert(ObjectType type, DataContainer dc)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to construct insert stmt
        // using values from data container

        fireEvent(getEvent(type, CompoundType.INSERT), dc, dc);
    }


    /**
     *  This obtains the update event for the passed in type,
     *  uses the DataContainer for the bind information, and
     *  the performs the update
     *
     *  @param type The ObjectType to use to look up the update statement
     *  @param dc The DataContainer to use to bind the variables in the
     *            update statements
     */
    public void doUpdate(ObjectType type, DataContainer dc)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to construct update stmt
        // using values from data container

        fireEvent(getEvent(type, CompoundType.UPDATE), dc, dc, true);
    }


    /**
     *  This obtains the delete event for the passed in type,
     *  uses the DataContainer for the bind information, and
     *  the performs the delete
     *
     *  @param type The ObjectType to use to look up the update statement
     *  @param dc The DataContainer to use to bind the variables in the
     *            delete statements
     */
    public void doDelete(ObjectType type, DataContainer dc)
        throws PersistenceException {
        // use metadata to construct delete stmt using values from data
        // container DataStore uses static metadata and connnection pooling to
        // get actual database connection

        fireEvent(getEvent(type, CompoundType.DELETE), dc, dc);
    }


    /**
     *  This obtains the delete event for the passed in type,
     *  uses the DataContainer for the bind information, and
     *  the performs the delete
     *
     *  @param type The ObjectType to use to look up the update statement
     *  @param attr the name of the attribute to be retrieved
     *  @param dc The DataContainer to use to bind the variables in the
     *            delete statements
     */

    public boolean doRetrieve(ObjectType type, String attr, DataContainer dc) {
        //Event event = m_builder.buildRetrieveEvent(type, attr);

        Event event = type.getEvent(CompoundType.RETRIEVE_ATTRIBUTES);

        if (event == null) {
            return false;
        }

        return fireEvent(event, dc, dc);
    }

    /**
     *  This obtains the roleRetrieve Event for the passed in type and role,
     *  uses the source to populate the bind variables and puts the result
     *  in to the target.
     *
     *  @param type the ObjectType to retrieve the event for
     *  @param role The role to retrieve
     *  @param source The DataContainer used to populate the bind variables
     *  @param target The DataContainer that receives the output of the query
     *  @return returns true iff at least one row is returned
     */
    public boolean doRoleRetrieve(ObjectType type, String role,
                                  DataContainer source,
                                  DataContainer target)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to construct select stmt
        // based on source (container) object and related role object. Plus
        // use values from container object to support where clause

        return fireEvent(getEvent(type, role, Property.RETRIEVE),
                         source, source);
    }

    /**
     *  This obtains the roleRetrieve Event for the passed in type and role,
     *  uses the source to populate the bind variables and puts the result
     *  in to the target.
     *
     *  @param type the ObjectType to retrieve the event for
     *  @param role The role to retrieve
     *  @param source The DataContainer used to populate the bind variables
     *  @param target The DataContainer that receives the output of the query
     */
    public void doRoleAdd(ObjectType type, String role,
                          DataContainer source, DataContainer target)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to construct insert/update
        // stmt based on source (container) object and related role object.
        // Plus use values from source object and target object to support
        // where clause

        Property prop = type.getProperty(role);
        Association assn = prop.getAssociation();
        if (prop.isCollection() && assn == null) {
            DataAssociationImpl da = (DataAssociationImpl) source.get(role);
            da.setDataContainer(target);
            fireEvent(getEvent(type, prop, Property.ADD), source, source);
            da.setDataContainer(null);
        } else if (prop.isCollection() && assn != null) {
            fireEvent(getEvent(type, prop, Property.ADD),
                      DataContainer.join(target, source),
                      target);
        } else {
            fireEvent(getEvent(type, prop, Property.ADD), source, source);
        }
    }


    /**
     *  This obtains the roleRetrieve Event for the passed in type and role,
     *  uses the source to populate the bind variables and puts the result
     *  in to the target.
     *
     *  @param type the ObjectType to retrieve the event for
     *  @param role The role to retrieve
     *  @param source The DataContainer used to populate the bind variables
     *  @param target The DataContainer that receives the output of the query
     */
    public void doRoleRemove(ObjectType type, String role,
                             DataContainer source, DataContainer target)
        throws PersistenceException {
        // DataStore uses static metadata and connnection pooling to get
        // actual database connection use metadata to construct delete/update
        // stmt based on source (container) object and related role object.
        // Plus use values from source object and target object to support
        // where clause

        Property prop = type.getProperty(role);
        Association assn = prop.getAssociation();
        if (prop.isCollection() && assn == null) {
            DataAssociationImpl da = (DataAssociationImpl) source.get(role);
            da.setDataContainer(target);
            fireEvent(getEvent(type, prop, Property.REMOVE), source, source);
            da.setDataContainer(null);
        } else if (prop.isCollection() && assn != null) {
            fireEvent(getEvent(type, prop, Property.REMOVE),
                      DataContainer.join(target, source),
                      target);
        } else {
            fireEvent(getEvent(type, prop, Property.REMOVE), source, source);
        }
    }


    /**
     * This objtains the clear Event for the passed in type and role, and
     * fires the event using the given data container.
     *
     * @param type The ObjectType used to obtain the event.
     * @param role The role to clear.
     * @param data The data container of the associated data object.
     **/

    public void doRoleClear(ObjectType type, String role, DataContainer data) {
        Property prop = type.getProperty(role);
        fireEvent(getEvent(type, prop, Property.CLEAR), data, data);
    }

}
