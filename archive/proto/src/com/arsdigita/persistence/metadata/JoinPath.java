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

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;

/**
 * JoinPath defines a complete path from one ObjectType or table to another,
 * allowing metadata-driven SQL to create queries across multiple tables on
 * the fly. A path is composed of 1 or more JoinElements, specifying the
 * particular columns to join, and in what order.
 *
 * @author Patrick McNeill
 * @version $Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/JoinPath.java#2 $
 * @since 4.6
 *
 * @invariant getPath() != null
 **/

public class JoinPath extends Element {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/metadata/JoinPath.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 09:48:41 $";

    private List m_path;
    // a List of JoinElements

    /**
     * Create a JoinPath, with the path to be created later.
     **/
    public JoinPath() {
        this(new ArrayList());
    }

    /**
     * Create a JoinPath with the given path, a List of JoinElements.
     *
     * @param path a list of JoinElements
     *
     * @pre path != null
     **/
    public JoinPath(List path) {
        m_path = path;
    }

    /**
     * Adds a new JoinElement to the end of the path
     *
     * @param element the JoinElement to append to the path
     * @pre element != null
     **/
    public void addJoinElement(JoinElement element) {
        if (m_path.size() == 0) {
            setLineInfo(element);
        }

        m_path.add(element);
    }

    /**
     * Adds a new JoinElement to the end of the path, created from two
     * columns.
     *
     * @param from the column to start the join
     * @param to the column where the join ends
     * @pre from != null && to != null
     **/
    public void addJoinElement(Column from, Column to) {
        addJoinElement(new JoinElement(from, to));
    }

    /**
     * Returns an Iterator containing all the JoinElements that are part of
     * this JoinPath.
     *
     * @return An Iterator of JoinElements.
     *
     * @see JoinElement
     **/

    public Iterator getJoinElements() {
        return m_path.iterator();
    }

    public JoinElement getJoinElement(int index) {
        return (JoinElement) m_path.get(index);
    }

    /**
     * Specify the entire join path
     *
     * @param path a List of JoinElements
     * @pre path != null
     **/
    protected void setPath(List path) {
        m_path = path;
        setLineInfo((JoinElement) path.get(0));
    }

    /**
     * Returns the current path
     *
     * @return the current path
     **/
    public List getPath() {
        return m_path;
    }

    /**
     * Outputs a serialized representation of this JoinPath.
     *
     * @param out The PrintStream to use for output.
     **/

    void outputPDL(PrintStream out) {
        for (Iterator it = getJoinElements(); it.hasNext(); ) {
            JoinElement je = (JoinElement) it.next();
            je.outputPDL(out);
            if (it.hasNext()) {
                out.print(", ");
            }
        }
    }

    void generateForeignKeys(boolean cascade, boolean isCollection) {
        if (m_path.size() == 1) {
            JoinElement je = getJoinElement(0);
            if (isCollection) {
                if (je.getFrom().isUniqueKey() &&
                    !je.getTo().isForeignKey()) {
                    new ForeignKey(null, je.getTo(), je.getFrom(), cascade);
                }
            } else {
                if (je.getTo().isUniqueKey() &&
                    !je.getFrom().isForeignKey()) {
                    new ForeignKey(null, je.getFrom(), je.getTo(), cascade);
                }
            }
        } else if (m_path.size() == 2) {
            JoinElement first = getJoinElement(0);
            JoinElement second = getJoinElement(1);

            // Set up foreign keys
            if (first.getFrom().isUniqueKey() &&
                second.getTo().isUniqueKey()) {
                if (!first.getTo().isForeignKey()) {
                    new ForeignKey(null, first.getTo(), first.getFrom(),
                                   cascade);
                }
                if (!second.getFrom().isForeignKey()) {
                    new ForeignKey(null, second.getFrom(), second.getTo(),
                                   cascade);
                }
            }


            // Set up unique keys
            if (isCollection) {
                if (first.getFrom().isUniqueKey() &&
                    second.getTo().isUniqueKey()) {
                    Column[] cols = new Column[] {first.getTo(),
                                                  second.getFrom()};
                    if (cols[0] == cols[1]) {
                        cols[0].error("Duplicate column");
                    }
                    Table table = first.getTo().getTable();
                    if (table.getPrimaryKey() == null) {
                        table.setPrimaryKey(new UniqueKey(table, null, cols));
                    }
                }
            } else {
                if (first.getFrom().isUniqueKey()) {
                    Table table = first.getTo().getTable();
                    if (table.getPrimaryKey() == null) {
                        table.setPrimaryKey(new UniqueKey(null,
                                                          first.getTo()));
                    }
                }
            }
        }
    }

}
