/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/30 $
 **/

class QValue {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/QValue.java#2 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
