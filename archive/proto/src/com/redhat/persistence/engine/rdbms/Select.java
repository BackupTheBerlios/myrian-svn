package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.common.*;
import com.redhat.persistence.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/Select.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private Join m_join;
    private Expression m_filter;
    private ArrayList m_selections = new ArrayList();
    private HashMap m_aliases = new HashMap();
    private ArrayList m_order = new ArrayList();
    private HashSet m_ascending = new HashSet();
    private Integer m_offset = null;
    private Integer m_limit = null;
    private boolean m_isCount = false;

    public Select(Join join, Expression filter) {
        this(join, filter, new Environment(null));
    }

    public Select(Join join, Expression filter, Environment env) {
        super(env);
        m_join = join;
        m_filter = filter;
    }

    public Join getJoin() {
        return m_join;
    }

    public Expression getFilter() {
        return m_filter;
    }

    public void addSelection(Path path, String alias) {
        if (!m_selections.contains(path)) {
            m_selections.add(path);
            m_aliases.put(path, alias);
        }
    }

    public String getAlias(Path path) {
        return (String) m_aliases.get(path);
    }

    public Collection getSelections() {
        return m_selections;
    }

    public void setCount(boolean value) {
        m_isCount = value;
    }

    public boolean isCount() {
        return m_isCount;
    }

    public void addOrder(Expression expr, boolean isAscending) {
        if (m_order.contains(expr)) {
            throw new IllegalArgumentException
                ("already ordered by this path");
        }

        m_order.add(expr);
        if (isAscending) {
            m_ascending.add(expr);
        }
    }

    public boolean isAscending(Expression expr) {
        return m_ascending.contains(expr);
    }

    public Collection getOrder() {
        return m_order;
    }

    public Integer getOffset() {
        return m_offset;
    }

    public void setOffset(Integer offset) {
        m_offset = offset;
    }

    public Integer getLimit() {
        return m_limit;
    }

    public void setLimit(Integer limit) {
        m_limit = limit;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
