/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.common;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class SQL {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/common/SQL.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private SQLToken m_first;
    private SQLToken m_last;

    public SQL() {
        m_first = null;
        m_last = null;
    }

    public void append(SQLToken sql) {
        if (m_first == null) {
            m_first = sql;
            m_last = sql;
        } else {
            m_last.m_next = sql;
            sql.m_previous = m_last;
            m_last = sql;
        }
    }

    public SQLToken getFirst() {
        return m_first;
    }

    public SQLToken getLast() {
        return m_last;
    }

    public List getBindings() {
        return getBindings(m_first, null);
    }

    public static final List getBindings(SQLToken start, SQLToken end) {
        ArrayList result = new ArrayList();
        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isBind()) {
                result.add(Path.get(t.getImage()));
            }
        }
        return result;
    }

    public String toString() {
        return toString(m_first, null);
    }

    public static final String toString(SQLToken start, SQLToken end) {
        StringBuffer result = new StringBuffer();
        for (SQLToken t = start; t != end; t = t.getNext() ) {
            result.append(t.getImage());
        }
        return result.toString();
    }

}
