package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/05 $
 **/

class Condition {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Condition.java#2 $ by $Author: rhs $, $DateTime: 2003/02/05 18:34:37 $";

}

class EqualsCondition extends Condition {

    private Path m_left;
    private Path m_right;

    public EqualsCondition(Path left, Path right) {
        m_left = left;
        m_right = right;
    }

    public Path getLeft() {
        return m_left;
    }

    public Path getRight() {
        return m_right;
    }

    public String toString() {
        return m_left + " = " + m_right;
    }

}
