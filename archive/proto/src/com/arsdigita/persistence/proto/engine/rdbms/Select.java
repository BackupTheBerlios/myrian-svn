package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Select
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/05 $
 **/

class Select extends Operation {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Select.java#3 $ by $Author: rhs $, $DateTime: 2003/02/05 21:09:04 $";

    private ArrayList m_selections = new ArrayList();
    private Join m_join;

    public Select(Join join) {
        m_join = join;
    }

    public void addSelection(Path path) {
        if (!m_selections.contains(path)) {
            m_selections.add(path);
        }
    }

    public Collection getSelections() {
        return m_selections;
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

        return result.toString();
    }

}
