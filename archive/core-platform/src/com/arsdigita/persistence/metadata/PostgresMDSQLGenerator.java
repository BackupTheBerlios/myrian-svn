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


import com.arsdigita.persistence.oql.Query;
import com.arsdigita.util.PriorityQueue;
import com.arsdigita.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.apache.log4j.Category;

/**
 * A interface that defines an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the 
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see ObjectEvent ).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#3 $
 * @since 4.6.3
 */

class PostgresMDSQLGenerator extends BaseMDSQLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#3 $ by $Author: randyg $, $DateTime: 2002/07/18 15:00:29 $";

    private static Category s_log = 
        Category.getInstance(PostgresMDSQLGenerator.class);

    /**
     * Populate the "joins", "tables" and "columns" lists with data for the 
     * attributes that need to be aggressively loaded.
     */
    protected void addAggressiveLoads(Operation operation,
                                      ObjectType type,
                                      List joins,
                                      List columns,
                                      List tables,
                                      Property baseProp) {
        // Steps to follow:
        // 1) compile a list of all aggressive loads
        // 2) for each aggressive load path:
        //    A) loop over each path element:
        //       a) walk the inheritance hierarchy of the previous datatype
        //          (or start type for the first property) to determine
        //          where the foreign key is located.  join through each
        //          super-type traversal to maintain a path
        //       b) join through to the table of the current property's 
        //          datatype
        //       c) repeat until we hit the last element of the path
        //    B) add a mapping from the last table/column joined to to the
        //       aggressive load name

        ObjectType superType = type;
        Collection aggLoads = new ArrayList();
        Collection loadedIDColumns = new ArrayList();
        Map idColumns = new HashMap();
        Iterator aggs;
        int tableCounter = 0;
        int columnCounter = 0;

        // maps from partial property names to renamed SQL tables and columns
        Map loadedRequired = new HashMap();
        Map loadedOptional = new HashMap();
        Map loadedColumns = new HashMap();

        while (superType != null) {
            aggs = superType.getAggressiveLoads();

            while (aggs.hasNext()) {
                String[] aggLoad = (String[])aggs.next();

                if (checkAggressiveLoad(type, aggLoad)) {
                    aggLoads.add(aggLoad);
                }
            }

            superType = superType.getSupertype();
        }

        aggs = aggLoads.iterator();

        while (aggs.hasNext()) {
            String[] aggressive = (String[])aggs.next();
            List partialName = new ArrayList();

            Property prop;
            Column priorCol = Utilities.getColumn(type);

            if (priorCol == null) {
                //bad
                break;
            }

            String priorTable = priorCol.getTableName();
            ObjectType currentType = type;
            DataType dataType = type;
            boolean outerJoin = false;

            String currentTable = null;
            Column currentCol = null;

        aggLoop:
            for (int i = 0; i < aggressive.length; i++) {
                currentType = (ObjectType)dataType;
                partialName.add(aggressive[i]);

                prop = currentType.getDeclaredProperty(aggressive[i]);

                if (prop == null) {
                    superType = currentType.getSupertype();

                    do {
                        currentCol = Utilities.getColumn(superType);

                        if (currentCol == null) {  
                            s_log.error(superType.getQualifiedName() + " is " + 
                                        "not MDSQL ready.");

                            break aggLoop;
                        }

                        prop = superType.getDeclaredProperty(aggressive[i]);
                        superType = superType.getSupertype();

                        if (currentCol.equals(priorCol)) {
                            //not so bad
                            continue;
                        }

                        String key = StringUtils.join(partialName, ".") + " " +
                                     currentCol.getTableName();
                        Column tmpCol = (Column)loadedColumns.get(key);

                        if (tmpCol != null) {
                            priorCol = tmpCol;
                            priorTable = (String)loadedOptional.get(key);

                            if (priorTable == null) {
                                priorTable = (String)loadedRequired.get(key);
                            } else {
                                outerJoin = true;
                            }

                            continue;
                        }

                        currentTable = "aggressive" + tableCounter;
                        tableCounter++;

                        tables.add(currentCol.getTableName() + " " +
                                   currentTable);

                        loadedColumns.put(key, currentCol);

                        if (outerJoin) {
                            throw new Error("Not Yet Implemented because " +
                                            "Postgres does not support outer "+
                                            "joins.");
                            /*
                            loadedOptional.put(key, currentTable);
                            joins.add(priorTable + "." +
                                      priorCol.getColumnName() +
                                      " = " + currentTable + "." + 
                                      currentCol.getColumnName() + "(+)");
                            */
                        } else {
                            loadedRequired.put(key, currentTable);
                            joins.add(priorTable + "." +
                                      priorCol.getColumnName() +
                                      " = " + currentTable + "." + 
                                      currentCol.getColumnName());
                        }

                        priorTable = currentTable;
                        priorCol = currentCol;
                    } while ((superType != null) && (prop == null));
                }

                if (prop.isAttribute()) {
                    currentCol = prop.getColumn();

                    String colName = "aggressiveCol" + columnCounter;

                    columns.add(priorTable + "." + currentCol.getColumnName() +
                                " as " + colName);

                    Column mapCol = new Column(priorTable, colName,
                                               currentCol.getType(),
                                               currentCol.getSize());

                    if (baseProp == null) {
                        Mapping m = new Mapping(aggressive, mapCol);
                        m.setLineInfo(type);
                        mapCol.setLineInfo(type);
                        operation.addMapping(m);
                    } else {
                        String[] aggName = new String[aggressive.length + 1];

                        aggName[0] = baseProp.getName();

                        for (int j = 0; j < aggressive.length; j++) {
                            aggName[j+1] = aggressive[j];
                        }

                        Mapping m = new Mapping(aggName, mapCol);
                        m.setLineInfo(baseProp);
                        mapCol.setLineInfo(baseProp);
                        operation.addMapping(m);
                    }

                    if (currentType.isKeyProperty(prop)) {
                        loadedIDColumns.add(
                            StringUtils.join(Arrays.asList(aggressive), "."));
                    } else {
                        Property keyProperty =
                            Utilities.getKeyProperty(currentType);

                        if (keyProperty == null) {
                            s_log.error(currentType.getQualifiedName() + 
                                        " is not MDSQL ready.");

                            break;
                        }

                        // make a list from a list so we can remove from it
                        List baseName =
                            new ArrayList(Arrays.asList(aggressive));
                        baseName.remove(baseName.size() - 1);
                        baseName.add(keyProperty.getName());

                        String idName = StringUtils.join(baseName, ".");

                        if (idColumns.get(idName) == null) {
                            Column keyColumn = Utilities.getColumn(currentType);

                            if (keyColumn == null) {
                                s_log.error(currentType.getQualifiedName() + 
                                            " is not MDSQL ready.");

                                break;
                            }

                            keyColumn = new Column(priorTable,
                                                   keyColumn.getColumnName(),
                                                   keyColumn.getType(),
                                                   keyColumn.getSize());
                            keyColumn.setLineInfo(type);

                            idColumns.put(idName, keyColumn);
                        }
                    }

                    columnCounter++;

                    break;
                } else {
                    Iterator path = prop.getJoinPath().getJoinElements();

                    while (path.hasNext()) {
                        JoinElement je = (JoinElement)path.next();
                        currentCol = je.getTo();
                        priorCol = je.getFrom();

                        String key = StringUtils.join(partialName, ".") + " " +
                                     currentCol.getTableName();
                        Column tmpCol = (Column)loadedColumns.get(key);

                        if (tmpCol != null) {
                            priorCol = tmpCol;
                            priorTable = (String)loadedOptional.get(key);

                            if (priorTable == null) {
                                priorTable = (String)loadedRequired.get(key);
                            } else {
                                outerJoin = true;
                            }

                            continue;
                        }

                        currentTable = "aggressive" + tableCounter;
                        tableCounter++;

                        tables.add(currentCol.getTableName() + " " +
                                   currentTable);

                        loadedColumns.put(key, currentCol);
                        if (outerJoin || prop.isNullable()) {
                            outerJoin = true;
                            loadedOptional.put(key, currentTable);
                            joins.add(priorTable + "." +
                                      priorCol.getColumnName() + 
                                      " = " + currentTable + "." +
                                      currentCol.getColumnName() + "(+)");
                        } else {
                            loadedRequired.put(key, currentTable);
                            joins.add(priorTable + "." +
                                      priorCol.getColumnName() + 
                                      " = " + currentTable + "." +
                                      currentCol.getColumnName());
                        }

                        priorCol = currentCol;
                        priorTable = currentTable;
                    }

                    dataType = prop.getType();
                }
            }
        }

        Iterator idCols = idColumns.entrySet().iterator();

        while (idCols.hasNext()) {
            Map.Entry entry = (Map.Entry)idCols.next();

            String propName = (String)entry.getKey();

            if (loadedIDColumns.contains(propName)) {
                continue;
            }

            StringTokenizer tokens = new StringTokenizer(propName, ".");
            String[] propPath = new String[tokens.countTokens()];

            for (int i = 0; tokens.hasMoreTokens(); i++) {
                propPath[i] = tokens.nextToken();
            }

            String colName = "aggressiveCol" + columnCounter;
            columnCounter++;

            Column keycol = (Column)entry.getValue();
            Column newKeyCol = new Column(keycol.getTableName(),
                                          colName,
                                          keycol.getType());
            newKeyCol.setLineInfo(type);

            columns.add(keycol.getTableName() + "." + keycol.getColumnName() +
                        " as " + colName);

            if (baseProp == null) {
                Mapping m = new Mapping(propPath, newKeyCol);
                m.setLineInfo(type);
                operation.addMapping(m);
            } else {
                String[] aggName = new String[propPath.length + 1];

                aggName[0] = baseProp.getName();

                for (int i = 0; i < propPath.length; i++) {
                    aggName[i+1] = propPath[i];
                }

                Mapping m = new Mapping(aggName, newKeyCol);
                m.setLineInfo(baseProp);
                operation.addMapping(m);
            }
        }
    }
}
