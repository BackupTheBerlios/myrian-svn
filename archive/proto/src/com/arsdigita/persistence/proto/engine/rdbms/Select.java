package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2003/02/26 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#8 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private ArrayList m_selections = new ArrayList();
    private HashMap m_aliases = new HashMap();
    private Join m_join;
    private Condition m_condition;

    public Select(Join join, Condition condition) {
        this(join, condition, new Environment());
    }

    public Select(Join join, Condition condition, Environment env) {
        super(env);
        m_join = join;
        m_condition = condition;
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

    public Join getJoin() {
        return m_join;
    }

    public Condition getCondition() {
        return m_condition;
    }

    void write(SQLWriter w) {
        w.write(this);
    }

}
