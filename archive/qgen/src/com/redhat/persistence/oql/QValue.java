package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/03 $
 **/

class QValue {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QValue.java#3 $ by $Author: rhs $, $DateTime: 2004/03/03 08:29:14 $";

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

    boolean isNullable() {
        QFrame frame = m_frame.getDuplicate();
        if (frame.isOuter()) { return true; }
        if (frame.getTable() == null) { return true; }
        Root root = frame.getGenerator().getRoot();
        Table t = root.getTable(frame.getTable());
        if (t == null) { return true; }
        Column col = t.getColumn(m_column);
        return col.isNullable();
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
