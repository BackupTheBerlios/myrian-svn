package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/14 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#6 $ by $Author: rhs $, $DateTime: 2003/02/14 16:46:06 $";

    private ArrayList m_selections = new ArrayList();
    private Join m_join;
    private Condition m_condition;

    public Select(Join join, Condition condition) {
        m_join = join;
        m_condition = condition;
    }

    public void addSelection(Path path) {
        if (!m_selections.contains(path)) {
            m_selections.add(path);
        }
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
