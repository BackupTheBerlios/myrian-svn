package com.redhat.persistence.oql;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/02/21 $
 **/

class QValue {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QValue.java#1 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

    private QFrame m_frame;
    private String m_column;

    QValue(QFrame frame, String column) {
        m_frame = frame;
        m_column = column;
    }

    QFrame getFrame() {
        return m_frame;
    }

    String getTable() {
        return m_frame.getTable();
    }

    String getColumn() {
        return m_column;
    }

    public String toString() {
        // XXX: this handles literals
        if (m_frame.getType() == null) {
            return m_column;
        } else {
            return m_frame.alias() + "." + m_column;
        }
    }

}
