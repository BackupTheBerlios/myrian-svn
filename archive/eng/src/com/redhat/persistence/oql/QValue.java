/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class QValue {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/QValue.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private QFrame m_frame;
    private String m_column = null;
    private Code m_sql = null;
    private Expression m_expression = null;

    QValue(QFrame frame, String column) {
        m_frame = frame;
        m_column = column;
    }

    QValue(QFrame frame, Code sql) {
        m_frame = frame;
        m_sql = sql;
    }

    QValue(QFrame frame, Expression expr) {
        m_frame = frame;
        m_expression = expr;
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
        } else if (m_expression != null) {
            return m_expression.emit(m_frame.getGenerator());
        } else {
            return new Code(m_frame.alias() + "." + m_column);
        }
    }

    public String toString() {
        if (m_sql != null) {
            return m_sql.toString();
        } else if (m_expression != null) {
            return m_expression.toString();
        } else {
            return m_frame.alias() + "." + m_column;
        }
    }

}
