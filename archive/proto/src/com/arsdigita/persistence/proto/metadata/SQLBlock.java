package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;
import java.util.*;

/**
 * SQLBlock
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

public class SQLBlock {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/SQLBlock.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private String m_sql;
    private ArrayList m_bindings = new ArrayList();
    private ArrayList m_assigns = new ArrayList();
    private HashMap m_mappings = new HashMap();
    private HashMap m_types = new HashMap();

    public class Assign {

        private int m_index;
        private int m_begin;
        private int m_end;
        private ArrayList m_bindings = new ArrayList();

        private Assign(int index, int begin, int end) {
            m_index = index;
            m_begin = begin;
            m_end = end;
        }

        public boolean isFirst() {
            return m_index == 0;
        }

        public boolean isLast() {
            return m_index == m_assigns.size() - 1;
        }

        public int getBegin() {
            return m_begin;
        }

        public int getEnd() {
            return m_end;
        }

        public List getBindings() {
            return m_bindings;
        }

        public void addBinding(Path p) {
            m_bindings.add(p);
        }

        public String toString() {
            return m_sql.substring(m_begin, m_end);
        }

    }

    public SQLBlock(String sql) {
        m_sql = sql;
    }

    public String getSQL() {
        return m_sql;
    }

    public void addBinding(Path path) {
        m_bindings.add(path);
    }

    public List getBindings() {
        return m_bindings;
    }

    public String getBegin() {
        if (m_assigns.size() == 0) {
            return m_sql;
        } else {
            return m_sql.substring(0, ((Assign) m_assigns.get(0)).getBegin());
        }
    }

    public String getEnd() {
        if (m_assigns.size() == 0) {
            return "";
        } else {
            return m_sql.substring
                (((Assign) m_assigns.get(m_assigns.size() - 1)).getEnd());
        }
    }

    public Assign addAssign(int begin, int end) {
        Assign result = new Assign(m_assigns.size(), begin, end);
        m_assigns.add(result);
        return result;
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
        return m_sql + "\nbindings = " + m_bindings;
    }

}
