package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2003/02/28 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#9 $ by $Author: rhs $, $DateTime: 2003/02/28 19:58:14 $";

    private Join m_join;
    private Condition m_condition;
    private ArrayList m_selections = new ArrayList();
    private HashMap m_aliases = new HashMap();
    private ArrayList m_order = new ArrayList();
    private HashSet m_ascending = new HashSet();

    public Select(Join join, Condition condition) {
        this(join, condition, new Environment());
    }

    public Select(Join join, Condition condition, Environment env) {
        super(env);
        m_join = join;
        m_condition = condition;
    }

    public Join getJoin() {
        return m_join;
    }

    public Condition getCondition() {
        return m_condition;
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

    public void addOrder(Path path, boolean isAscending) {
        if (m_order.contains(path)) {
            throw new IllegalArgumentException
                ("already ordered by this path");
        }

        m_order.add(path);
        if (isAscending) {
            m_ascending.add(path);
        }
    }

    public boolean isAscending(Path path) {
        return m_ascending.contains(path);
    }

    public Collection getOrder() {
        return m_order;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
