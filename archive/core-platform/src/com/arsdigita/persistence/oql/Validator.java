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

package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.metadata.ObjectType;
import java.util.*;

/**
 * Validator
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/08/14 $
 **/

class Validator extends Actor {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/oql/Validator.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private int m_nonOuter = 0;
    private Set m_connected = new HashSet();
    private Set m_tables = new HashSet();

    public void act(Query query) {
        query.traverse(this);
        if (m_nonOuter == 0) {
            query.error("There must be at least one table that is " +
                        "not outer joined.");
        }

        if (m_tables.size() == 0) {
            query.error("There must be at least one table in the query.");
        }

        Stack stack = new Stack();
        stack.add(m_tables.iterator().next());

        while (stack.size() > 0) {
            Table table = (Table) stack.pop();
            m_connected.add(table);

            for (Iterator it = table.getConditions().iterator();
                 it.hasNext(); ) {
                Condition cond = (Condition) it.next();
                Table toAdd;
                if (cond.getHead().getTable().equals(table)) {
                    toAdd = cond.getTail().getTable();
                } else {
                    toAdd = cond.getHead().getTable();
                }
                if (!m_connected.contains(toAdd)) {
                    stack.push(toAdd);
                }
            }
        }

        m_tables.removeAll(m_connected);
        if (m_tables.size() > 0) {
            query.error("The following tables could not be reached " +
                        m_tables + " starting from " + m_connected + ".");
        }

        ObjectType type = query.getObjectType();
        String baseTable = type.getReferenceKey() != null ?
            type.getReferenceKey().getTableName() : null;

        if (baseTable != null && query.getTable(baseTable) == null) {
            query.error("Missing base table: " + baseTable);
        }
    }

    public void act(Table table) {
        m_tables.add(table);

        if (!table.getNode().isOuter()) {
            m_nonOuter++;
        }

        if (table.getEntering().size() > 1) {
            table.getQuery().error("Table constrained more than once: " +
                                   table.getAlias() + " " +
                                   table.getEntering());
        }
    }

}
