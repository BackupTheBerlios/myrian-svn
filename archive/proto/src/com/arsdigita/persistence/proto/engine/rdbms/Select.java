package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/02/06 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#4 $ by $Author: rhs $, $DateTime: 2003/02/06 12:29:10 $";

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

    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("select ");

        if (m_selections.size() == 0) {
            result.append("*");
        } else {
            for (Iterator it = m_selections.iterator(); it.hasNext(); ) {
                Path path = (Path) it.next();
                result.append(path);
                if (it.hasNext()) {
                    result.append(",\n       ");
                }
            }
        }

        result.append("\nfrom ");
        result.append(m_join);

        if (m_condition != null) {
            result.append("\nwhere ");
            result.append(m_condition);
        }

        return result.toString();
    }

}
