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

import com.arsdigita.util.PriorityQueue;
import com.arsdigita.util.StringUtils;
import com.arsdigita.persistence.oql.Query;
import java.util.NoSuchElementException;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.log4j.Category;

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
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/BaseMDSQLGenerator.java#2 $
 * @since 4.6.3
 */
abstract class BaseMDSQLGenerator implements MDSQLGenerator {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/BaseMDSQLGenerator.java#2 $ by $Author: randyg $, $DateTime: 2002/07/18 15:00:29 $";

    private static final Category s_log =
        Category.getInstance(BaseMDSQLGenerator.class);

    private static final boolean useOptimizingGenerator(ObjectType type) {
        return type.getOption("OPTIMIZE").equals(Boolean.TRUE);
    }

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


    /**
     * Populate the "tables" and "columns" lists with data for the basic
     * attributes needed by this retrieve event.
     */
    protected boolean addAttributes(Operation operation,
                                    ObjectType type,
                                    Property baseProp,
                                    List columns,
                                    List tables) {
        Iterator attrs = type.getProperties();
        Column refkey = Utilities.getColumn(type);

        columns.add(refkey.getTableName() + "." + refkey.getColumnName());
        tables.add(refkey.getTableName());

        int numAttrs = 0;

        String[] propName;

        if (baseProp == null) {
            propName = new String[1];
            propName[0] = Utilities.getKeyProperty(type).getName();
        } else {
            propName = new String[2];
            propName[0] = baseProp.getName();
            propName[1] = Utilities.getKeyProperty(type).getName();
        }

        Mapping mapping = new Mapping(propName, refkey);
        if (baseProp == null) {
            mapping.setLineInfo(type);
        } else {
            mapping.setLineInfo(baseProp);
        }
        operation.addMapping(mapping);

    attrLoop:
        while (attrs.hasNext()) {
            Property prop = (Property)attrs.next();

            if (!prop.isAttribute() || type.isKeyProperty(prop)) {
                continue;
            }

            Column col = prop.getColumn();

            if (col == null) {
                // if an attribute has no associated column, we can't handle
                // this object type.  sorry.
//                s_log.warn(type.getName() + " has a key column defined, " +
//                           "but property " + prop.getName() + " does not");
                return false;
            }

            Iterator oldCols = columns.iterator();

            // can't just use contains here since we're renaming the columns
            while (oldCols.hasNext()) {
                String oldCol = (String)oldCols.next();

                if (oldCol.startsWith(
                    col.getTableName() + "." + col.getColumnName() + " ")) {
                    continue attrLoop;
                }
            }

            columns.add(col.getTableName() + "." + col.getColumnName() +
                        " as attribute" + numAttrs);

            col = new Column(col.getTableName(),
                             "attribute" + numAttrs,
                             col.getType(),
                             col.getSize());
            col.setLineInfo(type);

            numAttrs++;

            if (!tables.contains(col.getTableName())) {
                tables.add(col.getTableName());
            }

            if (baseProp == null) {
                propName = new String[1];
                propName[0] = prop.getName();
            } else {
                propName = new String[2];
                propName[0] = baseProp.getName();
                propName[1] = prop.getName();
            }

            Mapping m = new Mapping(propName, col);
            if (baseProp == null) {
                m.setLineInfo(type);
            } else {
                m.setLineInfo(baseProp);
            }

            operation.addMapping(m);
        }

        return true;
    }


    /**
     * Populate the "tables" and "joins" lists with data for the internal
     * join paths used by this objecttype.
     */
    protected void addJoinPaths(ObjectType type,
                                List joins,
                                List tables) {
        Iterator paths = type.getJoinPaths();
        Iterator elems;

        while (paths.hasNext()) {
            elems = ((JoinPath)paths.next()).getJoinElements();

            while (elems.hasNext()) {
                JoinElement elem = (JoinElement)elems.next();

                joins.add(elem.getFrom().getTableName() + "." + 
                          elem.getFrom().getColumnName() + " = " +
                          elem.getTo().getTableName() + "." +
                          elem.getTo().getColumnName());

                if (!tables.contains(elem.getFrom().getTableName())) {
                    tables.add(elem.getFrom().getTableName());
                }

                if (!tables.contains(elem.getTo().getTableName())) {
                    tables.add(elem.getTo().getTableName());
                }
            }
        }
    }


    /**
     * Populate the "tables" and "joins" lists with data to retrieve
     * the property baseProp
     */
    protected boolean addPropertyJoinPath(ObjectType type,
                                          Property baseProp,
                                          List joins,
                                          List tables) {
        JoinElement prior = null;
        String priorname = null;

        Iterator elems = baseProp.getJoinPath().getJoinElements();

        for (int i = 1; elems.hasNext(); i++) {
            JoinElement je = (JoinElement)elems.next();
            String nextname;
            String table1;
            String table2;

            if ((prior != null) &&
                !prior.getTo().getTableName().equals(
                    je.getFrom().getTableName())) {
                s_log.warn("Non-continuous JoinPath found to " +
                           type.getName());
                return false;
            }

            if (priorname == null) {
                priorname = "table0";
            }

            table1 = je.getFrom().getTableName() + " " + priorname;

            if (elems.hasNext()) {
                nextname = "table" + i;
                table2 = je.getTo().getTableName() + " " + nextname;
            } else {
                nextname = je.getTo().getTableName();
                table2 = nextname;
            }

            joins.add(priorname + "." + je.getFrom().getColumnName() +
                      " = " + nextname + "." + je.getTo().getColumnName());


            if (!tables.contains(table1)) {
                tables.add(table1);
            }

            if (!tables.contains(table2)) {
                tables.add(table2);
            }

            priorname = nextname;
            prior = je;
        }

        return true;
    }

    /**
     * Populate the "tables" and "joins" lists with data for the
     * join paths that are used internally to an objecttype.
     */
    protected boolean addSuperTypeJoinPaths(ObjectType type,
                                            List joins,
                                            List tables) {
        ObjectType obj = type.getSupertype();
        Column last = Utilities.getColumn(type);
        Column curr;

        // build up the joins we have to generate
        while (obj != null) {
            curr = Utilities.getColumn(obj);

            if (curr == null) {
                return false;
            }

            joins.add(last.getTableName() + "." + last.getColumnName() +
                      " = " + curr.getTableName() + "." + curr.getColumnName());

            if (!tables.contains(curr.getTableName())) {
                tables.add(curr.getTableName());
            }

            Iterator paths = obj.getJoinPaths();
    
            while (paths.hasNext()) {
                Iterator elems = ((JoinPath)paths.next()).getJoinElements();
    
                while (elems.hasNext()) {
                    JoinElement elem = (JoinElement)elems.next();
    
                    joins.add(elem.getFrom().getTableName() + "." + 
                              elem.getFrom().getColumnName() + " = " +
                              elem.getTo().getTableName() + "." +
                              elem.getTo().getColumnName());

                    if (!tables.contains(elem.getFrom().getTableName())) {
                        tables.add(elem.getFrom().getTableName());
                    }

                    if (!tables.contains(elem.getTo().getTableName())) {
                        tables.add(elem.getTo().getTableName());
                    }
                }
            }

            last = curr;
            obj = obj.getSupertype();
        }

        return true;
    }


    /**
     * Check that an aggressive load is valid.  I've split
     * this code out since the aggressive loading code is fairly long and
     * the error messages make it harder to read.  This also saves some 
     * processing by eliminating aggressive loads that won't work.
     */
    protected boolean checkAggressiveLoad(ObjectType type,
                                          String[] aggressive) {
        String name = StringUtils.join(Arrays.asList(aggressive), ".");

        for (int i = 0; i < aggressive.length; i++) {
            Property prop = type.getProperty(aggressive[i]);

            if (prop == null) {
                s_log.warn("Aggressive load " + name + " is invalid.  " +
                            aggressive[i] + " does not exist in " + 
                            type.getQualifiedName());

                return false;
            }

            if (prop.isCollection()) {
                s_log.error("Aggressive load " + name + " is invalid.  " +
                            aggressive[i] + " in " + type.getQualifiedName() + 
                            " is a collection property.");

                return false;
            }

            if (i == aggressive.length - 1) {
                if (!prop.isAttribute()) {
                    s_log.error("Aggressive load " + name + " is invalid.  " +
                                aggressive[i] + " in " + 
                                type.getQualifiedName() + 
                                " is not a simple type.");

                    return false;
                }

                if (prop.getColumn() == null) {
                    s_log.error("Aggressive load " + name + " is invalid.  " +
                                aggressive[i] + " in " + 
                                type.getQualifiedName() + 
                                " has no defined column.");

                    return false;
                }
            } else {
                if (prop.isAttribute()) {
                    s_log.error("Aggressive load " + name + " is invalid.  " +
                                aggressive[i] + " in " + 
                                type.getQualifiedName() + " is a simple " +
                                "type, but is not the last element.");

                    return false;
                }

                if (prop.getJoinPath() == null) {
                    s_log.error("Aggressive load " + name + " is invalid.  " +
                                aggressive[i] + " in " + 
                                type.getQualifiedName() + 
                                " has no join path.");

                    return false;
                }

                type = (ObjectType)prop.getType();
            }
        }

        return true;
    }

    /**
     * Populate the "joins", "tables" and "columns" lists with data for the 
     * attributes that need to be aggressively loaded.
     */
    protected abstract void addAggressiveLoads(Operation operation,
                                               ObjectType type,
                                               List joins,
                                               List columns,
                                               List tables,
                                               Property baseProp);


    /**
     * Generates the SQL for a retrieveAll event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @param baseProp the property whose join path we need to follow, nullable
     * @return the SQL to retrieve all objects of this type
     */
    protected Operation generateRetrieveSQL(ObjectType type,
                                            Property baseProp) {
        Operation operation = new Operation("select clause goes here");
        if (baseProp == null) {
            operation.setLineInfo(type);
        } else {
            operation.setLineInfo(baseProp);
        }
        StringBuffer sb = new StringBuffer();
        List tables = new ArrayList();
        List columns = new ArrayList();
        List joins = new ArrayList();

        if (Utilities.getColumn(type) == null) {
            return null;
        }

        // build up all our data so we can make a select statement...
        if (!addAttributes(operation, type, baseProp, columns, tables)) {
            return null;
        }

        addJoinPaths(type, joins, tables);

        if ((baseProp != null) &&
            !addPropertyJoinPath(type, baseProp, joins, tables)) {
            return null;
        }

        if (!addSuperTypeJoinPaths(type, joins, tables)) {
            return null;
        }

        addAggressiveLoads(operation, type, joins, columns, tables, baseProp);

        // now put everything together
        sb.append("select ").append(StringUtils.join(columns,", ")).append("\n");
        sb.append("from ").append(StringUtils.join(tables,", "));

        if (joins.size() > 0) {
            sb.append("\n where ").append(StringUtils.join(joins, " and\n"));
        }

        operation.setSQL(sb.toString());

        return operation;
    }

    protected Event generateRetrieve(ObjectType type) {
        Event ev = generateRetrieveUnoptimized(type);
        if (ev != null && useOptimizingGenerator(type)) {
            ev = generateRetrieveOptimized(type);
        }
        return ev;
    }

    protected Event generateRetrieveAll(ObjectType type) {
        Event ev = generateRetrieveAllUnoptimized(type);
        if (ev != null && useOptimizingGenerator(type)) {
            ev = generateRetrieveAllOptimized(type);
        }
        return ev;
    }

    protected Event generatePropertyRetrieve(ObjectType type, Property prop) {
        Event ev = generatePropertyRetrieveUnoptimized(type, prop);
        if (ev != null && useOptimizingGenerator(type)) {
            ev = generatePropertyRetrieveOptimized(type, prop);
        }
        return ev;
    }


    /**
     * Generates the SQL for a retrieve event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generateRetrieveOptimized(ObjectType type) {
        return generatePropertyRetrieveOptimized(type, null);
    }


    /**
     * Generates the SQL for a retrieve event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Operation generateRetrieveOperationOptimized(ObjectType type,
                                                           Property prop) {
        Query query = new Query(type);
        if (prop == null) {
            query.fetchDefault();
        } else {
            query.fetch(prop.getName());
        }

        query.generate();

        Operation op = query.getOperation();

        if (prop == null) {
            op.setLineInfo(type);
        } else {
            op.setLineInfo(prop);
        }

        return op;
    }


    /**
     * Generates the SQL for a retrieve event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generatePropertyRetrieveOptimized(ObjectType type,
                                                      Property prop) {
        Operation op = generateRetrieveOperationOptimized(type, prop);
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
        sb.append(keyMapping.getColumn().getTableName() + "." + 
                  keyMapping.getColumn().getColumnName());
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
    protected Event generateRetrieveAllOptimized(ObjectType type) {
        Operation op = generateRetrieveOperationOptimized(type, null);
        Event oe;
        oe = new Event();
        oe.setLineInfo(type);
        oe.addOperation(op);
        return oe;
    }


    /**
     * Generates the SQL for a retrieve event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @return the Event to retrieve an object of this type, or null
     */
    protected Event generateRetrieveUnoptimized(ObjectType type) {
        Operation sql = generateRetrieveSQL(type, null);

        if ((sql == null) || (Utilities.getKeyProperty(type) == null)) {
            return null;
        }

        Column refkey = Utilities.getColumn(type);
        StringBuffer sb = new StringBuffer();

        sb.append(sql.getSQL());

        if (sql.getSQL().indexOf(" where ") == -1) {
            sb.append("\nwhere ");
        } else {
            sb.append(" and\n");
        }

        sb.append(refkey.getTableName()).append(".")
          .append(refkey.getColumnName()).append(" = :")
          .append(Utilities.getKeyProperty(type).getName())
          .append("\n");

        sql.setSQL(sb.toString());

        Event oe;

        oe = new Event();
        oe.setLineInfo(type);
        oe.addOperation(sql);

        return oe;
    }


    /**
     * Generates the SQL for a retrieve all event for a particular object type.
     * 
     * @param type the object type to generate the event for
     * @return the Event to retrieve all objects of this type, or null
     */
    protected Event generateRetrieveAllUnoptimized(ObjectType type) {
        Operation sql = generateRetrieveSQL(type, null);

        if (sql == null) {
            return null;
        }

        Event oe;

        oe = new Event();
        oe.setLineInfo(type);
        oe.addOperation(sql);

        return oe;
    }

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
     * Generates a Retrieve event for a particular Property.
     *
     * @param prop the Property to generate a retrieve event for
     * @return the retrieve Event
     */
    protected Event generatePropertyRetrieveUnoptimized(ObjectType type, Property prop) {
        Operation sql = generateRetrieveSQL((ObjectType)prop.getType(), prop);
        StringBuffer sb = new StringBuffer();
        Column refkey = Utilities.getColumn(type);

        if ((sql == null) ||
            (refkey == null) ||
            (Utilities.getKeyProperty(type) == null)) {
            return null;
        }

        sb.append(sql.getSQL());

        if (sql.getSQL().indexOf(" where ") == -1) {
            sb.append("\nwhere ");
        } else {
            sb.append(" and\n");
        }

        sb.append("table0.")
          .append(refkey.getColumnName()).append(" = :")
          .append(Utilities.getKeyProperty(type).getName()).append("\n");

        Event event = new Event();
        event.setLineInfo(prop);

        sql.setSQL(sb.toString());

        event.addOperation(sql);

        return event;
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
                                                  Property prop) {
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

            // assume the mapping tables is je1.to/je2.from
            sb.append("insert into ")
              .append(je1.getTo().getTableName())
              .append(" (\n")
              .append(je1.getTo().getColumnName())
              .append(", ")
              .append(je2.getFrom().getColumnName())
              .append(")\nvalues (\n:")
              .append(refkey.getName())
              .append(", :")
              .append(prop.getName())
              .append(".")
              .append(typekey.getName())
              .append(")\n");
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
                                                     Property prop) {
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
     * Generates an Event of a particular type for a certain Property.
     * New event is automatically added to the Property.
     *
     * @param type the ObjectType the Property belongs to
     * @param prop the Property to generate an event for
     * @param eventType the Event type.  These are the types specified in
     *                  {@link com.arsdigita.persistence.metadata.Property}
     * @return the new Event, or null if it could not be created
     */
    public Event generateEvent(ObjectType type, Property prop, int eventType) {
        Event event = null;

        // don't waste time
        if (prop.isAttribute() || (prop.getJoinPath() == null)) {
            return null;
        }

        switch (eventType) {
            case Property.RETRIEVE:
                event = generatePropertyRetrieve(type, prop);
                break;
            case Property.ADD:
                if (prop.isCollection()) {
                    event = generateCollectionPropertyAdd(type, prop);
                } else {
                    event = generateSinglePropertyAdd(type, prop);
                }

                break;
            case Property.REMOVE:
                if (prop.isCollection()) {
                    event = generateCollectionPropertyRemove(type, prop);
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
}
