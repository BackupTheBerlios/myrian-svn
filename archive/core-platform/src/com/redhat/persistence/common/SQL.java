/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.common;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 **/

public class SQL {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/common/SQL.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
