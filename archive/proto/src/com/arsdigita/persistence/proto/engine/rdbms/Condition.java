package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.common.*;

/**
 * Condition
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/02/05 $
 **/

class Condition {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/Condition.java#3 $ by $Author: rhs $, $DateTime: 2003/02/05 21:09:04 $";

}

class AndCondition extends Condition {

    private Condition m_left;
    private Condition m_right;

    public AndCondition(Condition left, Condition right) {
        m_left = left;
        m_right = right;
    }

    public Condition getLeft() {
        return m_left;
    }

    public Condition getRight() {
        return m_right;
    }

    public String toString() {
        return m_left + "\nand " + m_right;
    }

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
