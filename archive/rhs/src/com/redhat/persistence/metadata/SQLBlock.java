/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence.metadata;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * SQLBlock
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class SQLBlock {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/SQLBlock.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

    private SQL m_sql;
    private ArrayList m_assigns = new ArrayList();

    private HashMap m_mappings = new HashMap();
    private HashMap m_types = new HashMap();

    public static class Assign {

        private SQLToken m_begin;
        private SQLToken m_end;

        private Assign(SQLToken begin, SQLToken end) {
            m_begin = begin;
            m_end = end;
        }

        public SQLToken getBegin() {
            return m_begin;
        }

        public SQLToken getEnd() {
            return m_end;
        }

        public String toString() {
            return SQL.toString(m_begin, m_end);
        }

    }


    public SQLBlock(SQL sql) {
        m_sql = sql;
    }

    public SQL getSQL() {
        return m_sql;
    }

    public void addAssign(SQLToken begin, SQLToken end) {
        m_assigns.add(new Assign(begin, end));
    }

    public Collection getAssigns() {
        return m_assigns;
    }

    public boolean hasMapping(Path path) {
        return m_mappings.containsKey(path);
    }

    public void addMapping(Path path, Path column) {
        if (hasMapping(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_mappings.put(path, column);
    }

    public void removeMapping(Path path) {
        m_mappings.remove(path);
    }

    public Path getMapping(Path path) {
        return (Path) m_mappings.get(path);
    }

    public Collection getPaths() {
        return m_mappings.keySet();
    }

    public boolean hasType(Path path) {
        return m_types.containsKey(path);
    }

    public void addType(Path path, int type) {
        if (hasType(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_types.put(path, new Integer(type));
    }

    public int getType(Path path) {
        return ((Integer) m_types.get(path)).intValue();
    }

    public String toString() {
        return m_sql.toString() + "\n assigns = " + m_assigns;
    }

}
