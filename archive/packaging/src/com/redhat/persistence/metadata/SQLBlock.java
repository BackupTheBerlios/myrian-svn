package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;
import java.util.*;

/**
 * SQLBlock
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class SQLBlock {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/metadata/SQLBlock.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
