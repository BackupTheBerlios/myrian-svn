package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/09 $
 **/

class QValue {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/QValue.java#4 $ by $Author: rhs $, $DateTime: 2004/03/09 21:48:49 $";

    private QFrame m_frame;
    private String m_column = null;
    private Code m_sql = null;

    QValue(QFrame frame, String column) {
        m_frame = frame;
        m_column = column;
    }

    QValue(QFrame frame, Code sql) {
        m_frame = frame;
        m_sql = sql;
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

    Code emit() {
        if (m_sql != null) {
            return m_sql;
        } else {
            return new Code(m_frame.alias() + "." + m_column);
        }
    }

    public String toString() {
        if (m_sql != null) {
            return m_sql.toString();
        } else {
            return m_frame.alias() + "." + m_column;
        }
    }

}
