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

import com.arsdigita.util.PriorityQueue;
import com.arsdigita.util.StringUtils;
import com.arsdigita.persistence.oql.Query;
import com.arsdigita.persistence.oql.NoMetadataException;
import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * A class that provides an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see Event ).
 *
 * Be aware that there are some restrictions on the use of this class.
 * First, it will not work for objects that have an object key composed of
 * more than one element.  Also, the RETRIEVE and RETRIEVE_ALL event
 * generators require that all of the attributes in the type hierarchy have
 * columns defined, not just the current object type (INSERT, UPDATE, and
 * DELETE do not have this restriction).  These restrictions may be removed
 * in the future, but we do not consider them to be essential at the moment.
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/BaseMDSQLGenerator.java#12 $
 * @since 4.6.3
 */
abstract class BaseMDSQLGenerator implements MDSQLGenerator {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/BaseMDSQLGenerator.java#12 $ by $Author: rhs $, $DateTime: 2002/09/16 18:59:05 $";

    private static final Logger s_log =
        Logger.getLogger(BaseMDSQLGenerator.class);


    //////////////////////////////////////////////////////////////
    // Methods for the MDSQLGenerator interface                 //
    //////////////////////////////////////////////////////////////

    /**
     * Generates an Event of a particular Event type for a certain
     * ObjectType.  New Event is automatically added to the object type
     * metadata.
     *
     * @param type the object type to create an event for
     * @param eventType the Event type.  These are the types specified
     * in {@link com.arsdigita.persistence.metadata.CompoundType}
     * @return the new Event, or null if it could not be created
     */
    public Event generateEvent(ObjectType type, int eventType) {
        Event event = null;

        switch (eventType) {
        case CompoundType.RETRIEVE:
            event = generateRetrieve(type);
            break;
        case CompoundType.RETRIEVE_ALL:
            event = generateRetrieveAll(type);
            break;
        case CompoundType.INSERT:
            event = generateInsert(type);
            break;
        case CompoundType.UPDATE:
            event = generateUpdate(type);
            break;
        case CompoundType.DELETE:
            event = generateDelete(type);
            break;
        }

        if (event != null) {
            type.setEvent(eventType, event);
        }


        return event;
    }


    /**
     *  This generates events specifically for associations.  That is,
     *  if an association requires a different type of event (e.g. an
     *  update) then this will call the associations event.  Otherwise,
     *  it delegates to {@link generateEvent}
     */
    public Event generateAssociationEvent(ObjectType type, int eventType) {
        if (type == null) {
            return null;
        }
        Event event = null;
        if (eventType == CompoundType.UPDATE) {
            event = generateAssociationUpdate(type);
        } else {
            event = generateEvent(type, eventType);
        }
        if (event != null) {
            type.setEvent(eventType, event);
        }

        return event;
    }


    /**
     * Generates an Event of a particular type for a certain Property.
     * New event is automatically added to the Property.
     *
     * @param type the ObjectType the Property belongs to
     * @param prop the Property to generate an event for
     * @param eventType the Event type.  These are the types specified in
     *                  {@link com.arsdigita.persistence.metadata.Property}
     * @return the new Event, or null if it could not be created
     */
    public Event generateEvent(ObjectType type, Property prop, int eventType,
                               ObjectType link) {
        Event event = null;

        // don't waste time
        if (prop.isAttribute() || (prop.getJoinPath() == null)) {
            if (eventType != Property.RETRIEVE ||
                prop.getColumn() == null) {
                return null;
            }
        }

        switch (eventType) {
        case Property.RETRIEVE:
            event = generatePropertyRetrieve(type, prop, link);
            break;
        case Property.ADD:
            if (prop.isCollection()) {
                event = generateCollectionPropertyAdd(type, prop, link);
            } else {
                event = generateSinglePropertyAdd(type, prop);
            }

            break;
        case Property.REMOVE:
            if (prop.isCollection()) {
                event = generateCollectionPropertyRemove(type, prop, link);
            } else {
                event = generateSinglePropertyRemove(type, prop);
            }

            break;
        case Property.CLEAR:
            if (prop.isCollection()) {
                event = generatePropertyClear(type, prop);
            } else {
                event = generateSinglePropertyRemove(type, prop);
            }

            break;
        }

        if (event != null) {
            prop.setEvent(eventType, event);
        }

        return event;
    }


    //////////////////////////////////////////////////////////////
    // Query generation methods                                 //
    //////////////////////////////////////////////////////////////

    /**
     * Generates the SQL for a retrieve event for a particular object type.
     *
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generateRetrieveAll(ObjectType type) {
        Operation op = generateRetrieveOperation(type, null, null);

        if (op == null) {
            return null;
        }

        Event oe;
        oe = new Event();
        oe.setLineInfo(type);
        oe.addOperation(op);
        return oe;
    }

    /**
     * Generates SQL for retrieving a single attribute of an object.
     * @param type The type of the object.
     * @param prop The attribute.
     **/

    protected Event generateAttributeRetrieve(ObjectType type, Property prop) {
        Column col = prop.getColumn();
        Table table = col.getTable();
        if (table.getPrimaryKey() == null) {
            return null;
        }
        Column keyCol = table.getPrimaryKey().getColumns()[0];
        Property key = (Property) type.getKeyProperties().next();

        StringBuffer sql = new StringBuffer();
        sql.append("select " + col.getName() +
                   "\nfrom " + table.getName() +
                   "\nwhere " + keyCol.getName() + " = :" + key.getName());

        Operation op = new Operation(sql.toString());
        op.setLineInfo(prop);
        op.addMapping(new Mapping(new String[] { prop.getName() },
                                  table.getName(),
                                  col.getName()));

        Event event = new Event();
        event.setLineInfo(prop);
        event.addOperation(op);

        return event;
    }

    /**
     * Generates the SQL for a retrieve event for a particular object type.
     *
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generatePropertyRetrieve(ObjectType type,
                                             Property prop,
                                             ObjectType link) {
        if (prop != null && prop.isAttribute()) {
            return generateAttributeRetrieve(type, prop);
        }

        Operation op = generateRetrieveOperation(type, prop, link);

        if (op == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        String baseSQL = op.getSQL();
        sb.append(baseSQL);
        if (baseSQL.indexOf(" where ") == -1) {
            sb.append("\nwhere ");
        } else {
            sb.append(" and\n");
        }

        Property p = (Property) type.getKeyProperties().next();
        Mapping keyMapping = op.getMapping(new String[] {p.getName()});
        sb.append(keyMapping.getTable() + "." + keyMapping.getColumn());
        sb.append(" = :" + p.getName() + "\n");

        op.setSQL(sb.toString());

        op.removeMapping(keyMapping);

        Event oe;
        oe = new Event();
        if (prop == null) {
            oe.setLineInfo(type);
        } else {
            oe.setLineInfo(prop);
        }
        oe.addOperation(op);
        return oe;
    }

    /**
     * Generates the SQL for a retrieve event for a particular object type.
     *
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generateRetrieve(ObjectType type) {
        return generatePropertyRetrieve(type, null, null);
    }


    /**
     * Generates the SQL for a retrieve event for a particular object type.
     *
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Operation generateRetrieveOperation(ObjectType type,
                                                  Property prop,
                                                  ObjectType link) {
        if (type.getReferenceKey() == null) {
            boolean found = false;
            for (Iterator it = type.getKeyProperties(); it.hasNext(); ) {
                Property key = (Property) it.next();
                if (key.isAttribute() && key.getColumn() == null) {
                    return null;
                } else if (key.isRole() && key.getJoinPath() == null) {
                    return null;
                }
                found = true;
            }

            if (!found) {
                return null;
            }
        }

        try {
            Query query = new Query(type);
            if (prop == null) {
                query.fetchDefault();
            } else {
                query.fetch(prop.getName());
                query.addLinkAttributes(prop, link);
            }

            query.generate();
            Operation op = query.getOperation();

            if (prop == null) {
                op.setLineInfo(type);
            } else {
                op.setLineInfo(prop);
            }

            return op;
        } catch (NoMetadataException e) {
            return null;
        }
    }


    //////////////////////////////////////////////////////////////
    // DML generation methods                                   //
    //////////////////////////////////////////////////////////////

    /**
     * Generates an Insert event for a particular object type.
     *
     * @param type the ObjectType
     * @return an INSERT Event
     */
    protected Event generateInsert(ObjectType type) {
        PriorityQueue pq = new PriorityQueue();
        Map columnMap = getTablesMap(type, pq);

        if (columnMap == null) {
            return null;
        }

        Event ev = new Event();
        ev.setLineInfo(type);

        Event oe = getSuperEvent(type, CompoundType.INSERT, ev);

        if ((type.getReferenceKey() == null) && (type.getSupertype() != null)) {
            return oe;
        }

        while (!pq.isEmpty()) {
            String tableName = (String)pq.dequeue();
            Iterator cols = ((Map)columnMap.get(tableName))
                .entrySet().iterator();

            StringBuffer sb = new StringBuffer();
            StringBuffer tmp = new StringBuffer();
            boolean first = true;

            sb.append("insert into ")
                .append(tableName)
                .append("(\n");

            while (cols.hasNext()) {
                Map.Entry me = (Map.Entry)cols.next();

                if (!first) {
                    sb.append(", ");
                    tmp.append(", ");
                } else {
                    first = false;
                }

                sb.append((String)me.getKey());
                tmp.append((String)me.getValue());
            }

            sb.append("\n) values (\n")
                .append(tmp.toString())
                .append(")");

            Operation block = new Operation(sb.toString());
            block.setLineInfo(type);
            oe.addOperation(block);
        }

        return oe;
    }

    /**
     * Generates an Update event for a particular object type.
     *
     * @param type the ObjectType
     * @return an UPDATE Event
     */
    protected Event generateUpdate(ObjectType type) {
        PriorityQueue pq = new PriorityQueue();
        Map columnMap = getTablesMap(type, pq);

        if (columnMap == null) {
            return null;
        }

        Event ev = new Event();
        ev.setLineInfo(type);

        Event oe = getSuperEvent(type, CompoundType.UPDATE, ev);

        if ((type.getReferenceKey() == null) && (type.getSupertype() != null)) {
            return oe;
        }

        while (!pq.isEmpty()) {
            String tableName = (String)pq.dequeue();
            Iterator cols = ((Map)columnMap.get(tableName))
                .entrySet().iterator();

            StringBuffer sb = new StringBuffer();
            String where = null;
            boolean first = true;

            sb.append("update ")
                .append(tableName)
                .append(" set \n");

            while (cols.hasNext()) {
                Map.Entry me = (Map.Entry)cols.next();
                String value = (String)me.getValue();

                if (value.equals(":" + Utilities.getKeyProperty(type).getName())) {
                    where = (String)me.getKey() + " = " + value;
                } else {
                    if (!first) {
                        sb.append(",\n");
                    } else {
                        first = false;
                    }

                    sb.append((String)me.getKey())
                        .append(" = ")
                        .append(value);
                }
            }

            // only updating the "id" column or no "id" col, so ignore it
            if (first || (where == null)) {
                continue;
            }

            sb.append("\nwhere ")
                .append(where);

            Operation block = new Operation(sb.toString());
            block.setLineInfo(type);
            oe.addOperation(block);
        }

        return oe;
    }


    /**
     * Generates a Delete event for a particular object type.
     *
     * @param type the ObjectType
     * @return an DELETE Event
     */
    protected Event generateDelete(ObjectType type) {
        Iterator attrs = type.getDeclaredProperties();
        PriorityQueue pq = new PriorityQueue(false);
        Map columnMap = getIDColumnsMap(type, pq);

        if ((columnMap == null) || (Utilities.getKeyProperty(type) == null)) {
            return null;
        }

        // some basic error checking
        while (attrs.hasNext()) {
            Property prop = (Property)attrs.next();

            if (prop.isAttribute() && (prop.getColumn() == null)) {
                //                s_log.warn(type.getName() + " has a key column defined, " +
                //                           "but property " + prop.getName() + " does not");
                return null;
            }
        }

        if ((type.getReferenceKey() == null) && (type.getSupertype() != null)) {
            Event ev = new Event();
            ev.setLineInfo(type);

            return getSuperEvent(type, CompoundType.DELETE, ev);
        }

        Event oe = new Event();
        oe.setLineInfo(type);

        while (!pq.isEmpty()) {
            String tableName = (String)pq.dequeue();
            StringBuffer sb = new StringBuffer();
            Column idCol = (Column)columnMap.get(tableName);

            sb.append("delete from ")
                .append(tableName)
                .append(" where ")
                .append(idCol.getColumnName())
                .append(" = :")
                .append(Utilities.getKeyProperty(type).getName());

            Operation block = new Operation(sb.toString());
            block.setLineInfo(type);
            oe.addOperation(block);
        }

        return getSuperEvent(type, CompoundType.DELETE, oe);
    }


    /**
     * Generates an Add event for a Property whose multiplicity is either
     * NULLABLE or REQUIRED.
     *
     * @param type the type that owns the Property
     * @param prop the Property to create an event for
     * @return an Add event for the property.
     */
    protected Event generateSinglePropertyAdd(ObjectType type, Property prop) {
        StringBuffer sb = new StringBuffer();
        List path = prop.getJoinPath().getPath();

        if (path.size() > 2) {
            s_log.warn("generateSinglePropertyAdd: " + prop.getName() +
                       " has a join path longer than 2");
            return null;
        }

        if (path.size() == 1) {
            Column from = ((JoinElement)path.get(0)).getFrom();
            Column refkey = Utilities.getColumn(type);
            Property typekey = Utilities.getKeyProperty((ObjectType)prop.getType());

            if ((typekey == null) ||
                (refkey == null) ||
                (Utilities.getKeyProperty(type) == null)) {
                s_log.warn("generateSinglePropertyAdd: " +
                           type.getName() + "." + prop.getName() +
                           "\ntypekey: " + typekey +
                           "\ntype: " + prop.getType().getName() +
                           "\nrefkey: " + refkey +
                           "\nUtilities.getKeyProperty(type): " +
                           Utilities.getKeyProperty(type));
                return null;
            }

            if (!refkey.getTableName().equals(from.getTableName())) {
                s_log.warn("generateSinglePropertyAdd: JoinPath for " +
                           prop.getName() + " in " + type.getQualifiedName() +
                           " does not start in the primary table.");
                return null;
            }

            sb.append("update ")
                .append(from.getTableName())
                .append("\nset ")
                .append(from.getColumnName())
                .append(" = :")
                .append(prop.getName())
                .append(".")
                .append(typekey.getName())
                .append("\nwhere ")
                .append(refkey.getTableName())
                .append(".")
                .append(refkey.getColumnName())
                .append(" = :")
                .append(Utilities.getKeyProperty(type).getName());
        } else {
            Column type1Ref = ((JoinElement)path.get(0)).getTo();
            Column type2Ref = ((JoinElement)path.get(1)).getFrom();
            Property type1Key = Utilities.getKeyProperty(type);
            Property type2Key =
                Utilities.getKeyProperty((ObjectType)prop.getType());

            if ((type1Key == null) || (type2Key == null)) {
                s_log.warn("generateSinglePropertyAdd: no key property " +
                           "defined for either " + type.getName() + " or " +
                           prop.getType().getName());

                return null;
            }

            if (!type1Ref.getTableName().equals(type2Ref.getTableName())) {
                s_log.warn("generateSinglePropertyAdd: JoinPath for " +
                           prop.getName() + " is not continuous.");
                return null;
            }

            sb.append("insert into ")
                .append(type1Ref.getTableName())
                .append(" (\n")
                .append(type1Ref.getColumnName())
                .append(", ")
                .append(type2Ref.getColumnName())
                .append(") values (\n:")
                .append(type1Key.getName())
                .append(", :")
                .append(prop.getName())
                .append(".")
                .append(type2Key.getName())
                .append(")");
        }

        Event event = new Event();
        event.setLineInfo(prop);
        Operation op = new Operation(sb.toString());
        op.setLineInfo(prop);
        event.addOperation(op);

        return event;
    }


    /**
     * Generates an Add event for a Property whose multiplicity is
     * COLLECTION.
     *
     * @param type the type that owns the Property
     * @param prop the Property to create an event for
     * @return an Add event for the property.
     */
    protected Event generateCollectionPropertyAdd(ObjectType type,
                                                  Property prop,
                                                  ObjectType link) {
        StringBuffer sb = new StringBuffer();
        List path = prop.getJoinPath().getPath();

        if (path.size() > 2) {
            s_log.warn("generateCollectionPropertyAdd: " + prop.getName() +
                       " has a join path not of length 1 or 2, " +
                       "but is a collection");
            return null;
        }

        JoinElement je1 = (JoinElement)path.get(0);

        Property refkey = Utilities.getKeyProperty(type);
        Property typekey = Utilities.getKeyProperty((ObjectType)prop.getType());
        Column refColumn = Utilities.getColumn(type);

        if ((typekey == null) || (refkey == null) || (refColumn == null)) {
            return null;
        }

        if (!refColumn.getTableName().equals(je1.getFrom().getTableName())) {
            s_log.warn("generateCollectionPropertyAdd: JoinPath for " +
                       prop.getName() +
                       " does not start in the primary table.");
            return null;
        }

        if (path.size() == 2) {
            JoinElement je2 = (JoinElement)path.get(1);

            if (!je1.getTo().getTableName().equals(
                                                   je2.getFrom().getTableName())) {
                s_log.warn("generateCollectionPropertyAdd: JoinPath for " +
                           prop.getName() + " is not continuous");
                return null;
            }

            Map columnValueMap = new HashMap();
            if (link != null) {
                String map = je1.getTo().getTableName();

                Iterator props = link.getDeclaredProperties();
                while (props.hasNext()) {
                    Property attr = (Property)props.next();
                    if (link.isKeyProperty(attr)) {
                        continue;
                    }
                    Column column = attr.getColumn();

                    if (column != null) {
                        if (column.getTableName().equals(map)) {
                            // if the column is in the table, add it
                            // to the list to be added.  This is used for
                            // simple types such as Integer and String
                            columnValueMap.put(column.getColumnName(),
                                               attr.getName());
                        } else {
                            s_log.warn("Link attribute " + attr.getName()
                                       + "  (" + column.getTableName()
                                       + "." + column.getColumnName()
                                       + ") is not in mapping table " + map);
                        }
                    } else {
                        addColumnValue(columnValueMap, attr, map);
                    }
                }
            }

            Iterator columnValues = columnValueMap.entrySet().iterator();
            StringBuffer valueBuffer = new StringBuffer();
            StringBuffer columnBuffer = new StringBuffer();
            while (columnValues.hasNext()) {
                Map.Entry me = (Map.Entry)columnValues.next();
                columnBuffer.append(", ").append((String)me.getKey());
                valueBuffer.append(", :").append((String)me.getValue());
            }

            // assume the mapping tables is je1.to/je2.from
            sb.append("insert into ")
                .append(je1.getTo().getTableName())
                .append(" (\n")
                .append(je1.getTo().getColumnName())
                .append(", ")
                .append(je2.getFrom().getColumnName());

            sb.append(columnBuffer);

            sb.append(")\nvalues (\n:")
                .append(refkey.getName())
                .append(", :")
                .append(prop.getName())
                .append(".")
                .append(typekey.getName());

            sb.append(valueBuffer);
            sb.append(")\n");
        } else {
            Column typeCol = Utilities.getColumn((ObjectType)prop.getType());

            sb.append("update ")
                .append(je1.getTo().getTableName())
                .append(" set\n")
                .append(je1.getTo().getColumnName())
                .append(" = :")
                .append(refkey.getName())
                .append("\nwhere ")
                .append(typeCol.getColumnName())
                .append(" = :")
                .append(prop.getName())
                .append(".")
                .append(typekey.getName());
        }

        Event event = new Event();
        event.setLineInfo(prop);
        Operation op = new Operation(sb.toString());
        op.setLineInfo(prop);
        event.addOperation(op);

        return event;
    }


    /**
     * This takes a property and, if it is properly formatted,
     * adds it column and value representation to the passed in map.
     * @param columnValueMap The map to add the column (key) and value (value).
     * @param attr The attribute to check.  This should have
     *             !attr.isAttribute() and should have a defined join path.
     *             attr.getType() should be an ObjectType
     * @param currentTable The table being updated/used.  If the column for
     *        the key property of the attribute is not a column for the
     *        current table then the attribute is skipped.
     *        If this is null then the check is skipped
     * @return This returns the value of currentTable if it was passed
     *         in or the value of the table used if currentTable == null.
     *         This returns null if the column could not be added to the map
     */
    private String addColumnValue(Map columnValueMap, Property attr,
                                  String currentTable) {
        String returnValue = null;
        // If we have something that is not an attribute
        // (e.g. a User) then we end up here and only
        // continue if the join path is property defined.
        if (!attr.isAttribute() && attr.getJoinPath() != null) {
            String keyUsed = null;
            Iterator i = ((ObjectType)attr.getType())
                .getKeyProperties();
            if (i.hasNext()) {
                Property keyProperty = (Property)i.next();
                keyUsed = keyProperty.getName();
                Iterator joinPath = attr.getJoinPath()
                    .getJoinElements();
                // we want the second of the first
                // element of the path
                Column from = ((JoinElement)joinPath.next()).getFrom();
                if (currentTable == null ||
                    from.getTableName().equals(currentTable)) {
                    returnValue = from.getTableName();
                    columnValueMap.put
                        (from.getColumnName(),
                         attr.getName() + "." +
                         keyProperty.getName());
                } else {
                    s_log.warn("Link attribute " +
                               keyProperty.getName()
                               + "  (" + from.getTableName()
                               + "." + from.getColumnName()
                               + ") is not in mapping table " + currentTable);
                }
            }
            if (i.hasNext()) {
                // if there is a second key then the code
                // may not work so we warn.
                s_log.warn
                    ("There are multiple keys for " +
                     "object type " +
                     ((ObjectType)attr.getType()).getName() +
                     ".  We are using key " + keyUsed + "." +
                     " This may or may not be appropriate. " +
                     "Please check to make sure that the " +
                     "generated SQL is correct.");
            }
        } else {
            // if there is not join path then there is
            // no metadata
            s_log.warn("No table/column definition for " +
                       "link attribute " + attr.getName());
        }
        return returnValue;
    }


    /**
     * Generates an Remove event for a Property whose multiplicity is
     * NULLABLE or REQUIRED.
     *
     * @param type the type that owns the Property
     * @param prop the Property to create an event for
     * @return an Add event for the property.
     */
    protected Event generateSinglePropertyRemove(ObjectType type,
                                                 Property prop) {
        StringBuffer sb = new StringBuffer();
        List path = prop.getJoinPath().getPath();

        if (path.size() > 2) {
            // this should probably be logged eventually
            s_log.warn("generateSinglePropertyRemove: " + prop.getName() +
                       " has a join path longer than 2");
            return null;
        }

        Column refkey = Utilities.getColumn(type);

        if ((refkey == null) || (Utilities.getKeyProperty(type) == null)) {
            return null;
        }

        if (path.size() == 1) {
            Column from = ((JoinElement)path.get(0)).getFrom();

            if (!refkey.getTableName().equals(from.getTableName())) {
                s_log.warn("generateSinglePropertyRemove: JoinPath for " +
                           prop.getName() +
                           " does not start in the primary table.");
                return null;
            }

            sb.append("update ")
                .append(from.getTableName())
                .append("\nset ")
                .append(from.getColumnName())
                .append(" = null\nwhere ")
                .append(refkey.getTableName())
                .append(".")
                .append(refkey.getColumnName())
                .append(" = :")
                .append(Utilities.getKeyProperty(type).getName());
        } else {
            Column type1Ref = ((JoinElement)path.get(0)).getTo();
            Column type2Ref = ((JoinElement)path.get(1)).getFrom();
            Property type1Key = Utilities.getKeyProperty(type);
            Property type2Key =
                Utilities.getKeyProperty((ObjectType)prop.getType());

            if ((type1Key == null) || (type2Key == null)) {
                s_log.warn("generateSinglePropertyAdd: no key property " +
                           "defined for either " + type.getName() + " or " +
                           prop.getType().getName());

                return null;
            }

            if (!type1Ref.getTableName().equals(type2Ref.getTableName())) {
                s_log.warn("generateSinglePropertyAdd: JoinPath for " +
                           prop.getName() + " is not continuous.");
                return null;
            }

            sb.append("delete from ")
                .append(type1Ref.getTableName())
                .append("\nwhere ")
                .append(type1Ref.getColumnName())
                .append(" = :")
                .append(type1Key.getName())
                .append(" and ")
                .append(type2Ref.getColumnName())
                .append(" = :")
                .append(prop.getName())
                .append(".")
                .append(type1Key.getName());
        }

        Event event = new Event();
        event.setLineInfo(prop);
        Operation op = new Operation(sb.toString());
        op.setLineInfo(prop);
        event.addOperation(op);

        return event;
    }


    /**
     * Generates an Remove event for a Property whose multiplicity is
     * COLLECTION.
     *
     * @param type the type that owns the Property
     * @param prop the Property to create an event for
     * @return an Add event for the property.
     */
    protected Event generateCollectionPropertyRemove(ObjectType type,
                                                     Property prop,
                                                     ObjectType link) {
        StringBuffer sb = new StringBuffer();
        List path = prop.getJoinPath().getPath();

        if (path.size() > 2) {
            s_log.warn("generateCollectionPropertyAdd: " + prop.getName() +
                       " has a join path not of length 1 or 2, " +
                       "but is a collection");
            return null;
        }

        JoinElement je1 = (JoinElement)path.get(0);

        Property refkey = Utilities.getKeyProperty(type);
        Property typekey = Utilities.getKeyProperty((ObjectType)prop.getType());
        Column refColumn = Utilities.getColumn(type);

        if ((typekey == null) || (refkey == null) || (refColumn == null)) {
            return null;
        }

        if (!refColumn.getTableName().equals(je1.getFrom().getTableName())) {
            s_log.warn("generateCollectionPropertyRemove: JoinPath for " +
                       prop.getName() +
                       " does not start in the primary table.");
            return null;
        }

        if (path.size() == 2) {
            JoinElement je2 = (JoinElement)path.get(1);

            if (!je1.getTo().getTableName().equals(
                                                   je2.getFrom().getTableName())) {
                s_log.warn("generateCollectionPropertyRemove: JoinPath for " +
                           prop.getName() + " is not continuous");
                return null;
            }

            // assume the mapping tables is je1.to/je2.from
            sb.append("delete from ")
                .append(je1.getTo().getTableName())
                .append(" where\n")
                .append(je1.getTo().getColumnName())
                .append(" = :")
                .append(refkey.getName())
                .append("\nand ")
                .append(je2.getFrom().getColumnName())
                .append(" = :")
                .append(prop.getName())
                .append(".")
                .append(typekey.getName());
        } else {
            Column typeCol = Utilities.getColumn((ObjectType)prop.getType());

            sb.append("update ")
                .append(je1.getTo().getTableName())
                .append(" set\n")
                .append(je1.getTo().getColumnName())
                .append(" = null\nwhere ")
                .append(typeCol.getColumnName())
                .append(" = :")
                .append(prop.getName())
                .append(".")
                .append(typekey.getName());
        }

        Event event = new Event();
        event.setLineInfo(prop);
        Operation op = new Operation(sb.toString());
        op.setLineInfo(prop);
        event.addOperation(op);

        return event;
    }


    /**
     * Generates an Clear event for a Property whose multiplicity is
     * COLLECTION.
     *
     * @param type the type that owns the Property
     * @param prop the Property to create an event for
     * @return an Add event for the property.
     */
    protected Event generatePropertyClear(ObjectType type, Property prop) {
        Event event = new Event();
        event.setLineInfo(prop);
        StringBuffer sb = new StringBuffer();
        List path = prop.getJoinPath().getPath();

        if (path.size() > 2) {
            s_log.warn("generateCollectionPropertyAdd: " + prop.getName() +
                       " has a join path not of length 1 or 2, " +
                       "but is a collection");
            return null;
        }

        JoinElement je1 = (JoinElement)path.get(0);
        Property refkey = Utilities.getKeyProperty(type);
        Column refColumn = Utilities.getColumn(type);

        if ((refkey == null) || (refColumn == null)) {
            return null;
        }

        if (!refColumn.getTableName().equals(je1.getFrom().getTableName())) {
            s_log.warn("generatePropertyClear: JoinPath for " +
                       prop.getName() +
                       " does not start in the primary table.");
            return null;
        }

        // assume the mapping tables is je1.to/je2.from

        if (path.size() == 2) {
            sb.append("delete from ")
                .append(je1.getTo().getTableName())
                .append(" where\n")
                .append(je1.getTo().getColumnName())
                .append(" = :")
                .append(refkey.getName());

            Operation op = new Operation(sb.toString());
            op.setLineInfo(prop);
            event.addOperation(op);

            // we assume true componentism here, if an entry is not in the
            // mapping table, it's gone
            if (prop.isComponent()) {
                JoinElement je2 = (JoinElement)path.get(1);
                sb = new StringBuffer();

                sb.append("delete from ")
                    .append(je2.getTo().getTableName())
                    .append("\nwhere ")
                    .append(je2.getTo().getColumnName())
                    .append(" not in (select ")
                    .append(je2.getFrom().getColumnName())
                    .append(" from ")
                    .append(je2.getFrom().getTableName())
                    .append(")");

                op = new Operation(sb.toString());
                op.setLineInfo(prop);
                event.addOperation(op);
            }
        } else {
            if (prop.isComponent()) {
                sb.append("delete from ")
                    .append(je1.getTo().getTableName())
                    .append("\nwhere ")
                    .append(je1.getTo().getColumnName())
                    .append(" = :")
                    .append(refkey.getName());
            } else {
                sb.append("update ")
                    .append(je1.getTo().getTableName())
                    .append(" set\n")
                    .append(je1.getTo().getColumnName())
                    .append(" = null\nwhere ")
                    .append(je1.getTo().getColumnName())
                    .append(" = :")
                    .append(refkey.getName());
            }

            Operation op = new Operation(sb.toString());
            op.setLineInfo(prop);
            event.addOperation(op);
        }


        return event;
    }


    /**
     * Generates an Update event that is used by associations for updating
     * link attributes.  This currently assumes the following:
     * <ul>
     * <li>The link attribute is within a mapping table</li>
     * <li>The join path is exactly two segments long</li>
     * <li>The mapping table is keyed off of the object key of the given
     *     property (e.g. it is keyed off of the "id" property).  This
     *     property must be the first (probably only) key property for the
     *     type.</li>
     * <li>The passed in object type has exactly two key properties (which
     * is standard for an association)</li>
     * <li>The "to" column of the first join element in the join path
     *     for the association is the column that can be used in the "where"
     *     segment of the DML</li>
     * <li></li>
     * </ul>
     *
     * @param link The object type containing information about the
     *             link attributes
     * @return an UPDATE Event
     */
    public Event generateAssociationUpdate(ObjectType link) {
        PriorityQueue pq = new PriorityQueue();
        Map columnMap = new HashMap();
        Map columns = new HashMap();
        Property propertyOne = null;
        Property propertyTwo = null;

        // The properties that are not key properties are the "link"
        // properties.  The first step is to loop through all
        // properties and get a list of columns and tables used by
        // the link attributes
        for (Iterator it = link.getProperties(); it.hasNext(); ) {
            Property property = (Property) it.next();
            if (link.isKeyProperty(property)) {
                if (propertyOne == null) {
                    propertyOne = property;
                } else if (propertyTwo == null) {
                    propertyTwo = property;
                } else {
                    throw new IllegalStateException
                        (link.getName() + " has more than two key properties."+
                         " It should only have two.");
                }
                continue;
            }

            // if we have reached this point then we are dealing with a link
            // attribute

            if (!property.isAttribute()) {
                Map map = new HashMap();
                String tableName = addColumnValue(map, property, null);
                Iterator columnValues = map.entrySet().iterator();
                int columnCount = 0;
                while (columnValues.hasNext()) {
                    columnCount++;
                    Map.Entry me = (Map.Entry)columnValues.next();

                    columns = (HashMap)columnMap.get(tableName);
                    if (columns == null) {
                        columns = new HashMap();
                        columnMap.put(tableName, columns);
                    }
                    columns.put((String)me.getKey(), ":" +
                                (String)me.getValue());
                }

                if (columnCount > 0 && pq != null) {
                    pq.enqueue(tableName, 0);
                }
            } else {
                Column col = property.getColumn();
                if (col == null) {
                    // if the link attribute does not have a column we
                    // cannot generate the DML
                    return null;
                }

                columns = (HashMap)columnMap.get(col.getTableName());
                if (columns == null) {
                    columns = new HashMap();
                    columnMap.put(col.getTableName(), columns);
                }

                columns.put(col.getColumnName(), ":" + property.getName());
                if (pq != null) {
                    pq.enqueue(col.getTableName(), 0);
                }
            }
        }


        Event ev = new Event();
        ev.setLineInfo(link);

        // now that we have a list of columns and tables, we can
        // actually start to generate the SQL
        while (!pq.isEmpty()) {
            String tableName = (String)pq.dequeue();
            Iterator cols = ((Map)columnMap.get(tableName))
                .entrySet().iterator();

            StringBuffer sb = new StringBuffer();
            String where = null;
            boolean first = true;

            sb.append("update ")
                .append(tableName)
                .append("\n set ");

            while (cols.hasNext()) {
                Map.Entry me = (Map.Entry)cols.next();
                String value = (String)me.getValue();

                if (!first) {
                    sb.append(",\n");
                } else {
                    first = false;
                }

                sb.append((String)me.getKey())
                    .append(" = ")
                    .append(value);
            }

            // now add the keys.  Since we assume that this is an association
            // going through a mapping table (where else would you have
            // a link attribute?) we are just using the two properties.

            first = true;
            for (Iterator it = link.getKeyProperties(); it.hasNext(); ) {
                Property p = (Property)it.next();
                if (first) {
                    sb.append("\n where ");
                    first = false;
                } else {
                    sb.append("\n and ");
                }

                // we should have two key properties.  They are both
                // compound types so we have to get the type and then
                // get the key property from it.
                ObjectType type = (ObjectType)p.getType();
                Property activeProperty = null;
                if (p.getName().equals(propertyOne.getName())) {
                    activeProperty = type.getProperty(propertyTwo.getName());
                } else {
                    activeProperty = type.getProperty(propertyOne.getName());
                }

                // we want the second item of the first path element
                Column columnToJoin = ((JoinElement)activeProperty
                                       .getJoinPath().getJoinElements()
                                       .next()).getTo();

                // This makes the assumption that things are keyed off of
                // the first key property returned by the type.  Most of
                // the time this should work becuase it will be the only
                // key property
                String key = ((Property)((ObjectType)activeProperty.getType())
                              .getKeyProperties().next()).getName();

                sb.append(columnToJoin.getQualifiedName() +
                          " = :" + p.getName() + "." + key);
            }

            Operation block = new Operation(sb.toString());
            block.setLineInfo(link);
            ev.addOperation(block);
        }

        return ev;
    }

    //////////////////////////////////////////////////////////////
    // Utility methods                                          //
    //////////////////////////////////////////////////////////////

    /**
     * Add all of the Operations from an ObjectType's super object type to
     * an object event, returning the object event.
     *
     * @param type the object type we're generating events for
     * @param event the event to add Operations to
     * @return the Event with the super Operations included
     */
    protected Event getSuperEvent(ObjectType type, int eventType, Event event) {
        ObjectType superType = type.getSupertype();

        if (superType == null) {
            return event;
        }

        Event superEvent = superType.getEvent(eventType);

        if (superEvent == null) {
            throw new IllegalStateException(type.getName() + " has no super " +
                                            "event of type " + eventType);
        }

        Iterator blocks = superEvent.getOperations();

        while (blocks.hasNext()) {
            event.addOperation((Operation)blocks.next());
        }

        return event;
    }


    /**
     * Create a map of table names to maps, which in turn contain mappings
     * from column to attribute bind variable.
     *
     * @param type the objecttype to generate the map for
     * @param pq a PriorityQueue (may be null) to store table ordering
     * @return the table map, or null
     */
    protected Map getTablesMap(ObjectType type, PriorityQueue pq) {
        Map columnMap = new HashMap();
        Iterator attrs = type.getDeclaredProperties();
        Map columns = new HashMap();
        List pathCols = new ArrayList();

        Column refkey = Utilities.getColumn(type);


        if ((refkey == null) || (Utilities.getKeyProperty(type) == null)) {
            return null;
        }

        String idName = Utilities.getKeyProperty(type).getName();

        columns.put(refkey.getColumnName(), ":" + idName);
        columnMap.put(refkey.getTableName(), columns);

        if (pq != null) {
            pq.enqueue(refkey.getTableName(), -1);
        }

        while (attrs.hasNext()) {
            Property prop = (Property)attrs.next();
            Column col = prop.getColumn();

            if (!prop.isAttribute()) {
                if (prop.getMultiplicity() != Property.REQUIRED) {
                    continue;
                }

                // abort if no join path or the JP is a mapping table.
                if ((prop.getJoinPath() == null) ||
                    (prop.getJoinPath().getPath().size() > 1)) {
                    continue;
                }

                col = ((JoinElement)prop.getJoinPath().getPath().get(0))
                    .getFrom();

                String typeName = Utilities.getKeyProperty
                    ((ObjectType)prop.getType()).getName();

                columns = (HashMap)columnMap.get(col.getTableName());

                if (columns == null) {
                    columns = new HashMap();
                    columnMap.put(col.getTableName(), columns);

                    if (pq != null) {
                        pq.enqueue(col.getTableName(), 0);
                    }
                }

                columns.put(col.getColumnName(), ":" + prop.getName() + "." +
                            typeName);

                continue;
            }

            if (col == null) {
                // if an attribute has no associated column, we can't handle
                // this object type.  sorry.
                //                s_log.warn(type.getName() + " has a key column defined, " +
                //                           "but property " + prop.getName() + " does not");
                return null;
            }

            columns = (HashMap)columnMap.get(col.getTableName());

            if (columns == null) {
                columns = new HashMap();
                columnMap.put(col.getTableName(), columns);

                if (pq != null) {
                    pq.enqueue(col.getTableName(), 0);
                }
            }

            columns.put(col.getColumnName(), ":" + prop.getName());
        }

        // get a list of the columns that appear in a join path.  since we
        // don't support paths of length > 1 for object types, we always
        // set these columns to ":id"
        Iterator paths = type.getJoinPaths();

        while (paths.hasNext()) {
            Iterator elems = ((JoinPath)paths.next()).getJoinElements();

            int distance = 0;

            while (elems.hasNext()) {
                JoinElement je = (JoinElement)elems.next();
                Column from = je.getFrom();
                Column to = je.getTo();

                distance++;

                columns = (HashMap)columnMap.get(from.getTableName());

                if (columns == null) {
                    columns = new HashMap();
                    columnMap.put(from.getTableName(), columns);

                    if (pq != null) {
                        pq.enqueue(from.getTableName(), distance);
                    }
                }

                columns.put(from.getColumnName(), ":" + idName);

                distance++;

                columns = (HashMap)columnMap.get(to.getTableName());

                if (columns == null) {
                    columns = new HashMap();
                    columnMap.put(to.getTableName(), columns);

                    if (pq != null) {
                        pq.enqueue(to.getTableName(), distance);
                    }
                }

                columns.put(to.getColumnName(), ":" + idName);
            }
        }

        return columnMap;
    }

    /**
     * Create a map of table names to ID column.  A map is used to ensure that
     * only one event is created per table.
     *
     * @param type the objecttype to generate the map for
     * @param pq the priority queue to store table ordering information in
     * @return the ID column map, or null
     */
    protected Map getIDColumnsMap(ObjectType type, PriorityQueue pq) {
        Map columns = new HashMap();
        List pathCols = new ArrayList();

        Column refkey = Utilities.getColumn(type);

        if (refkey == null) {
            return null;
        }

        columns.put(refkey.getTableName(), refkey);
        pq.enqueue(refkey.getTableName(), 0);

        Iterator paths = type.getJoinPaths();

        while (paths.hasNext()) {
            Iterator elems = ((JoinPath)paths.next()).getJoinElements();

            int distance = 0;

            while (elems.hasNext()) {
                JoinElement je = (JoinElement)elems.next();
                Column from = je.getFrom();
                Column to = je.getTo();

                distance++;
                if (columns.get(from.getTableName()) == null) {
                    pq.enqueue(from.getTableName(), distance);
                }

                distance++;
                if (columns.get(to.getTableName()) == null) {
                    pq.enqueue(to.getTableName(), distance);
                }

                columns.put(from.getTableName(), from);
                columns.put(to.getTableName(), to);
            }
        }

        return columns;
    }

}
