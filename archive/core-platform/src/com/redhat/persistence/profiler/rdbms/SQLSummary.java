/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.profiler.rdbms;

import com.arsdigita.util.UncheckedWrapperException;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.common.SQLToken;
import java.io.StringReader;
import java.util.HashMap;

/**
 * SQLSummary
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

class SQLSummary {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/profiler/rdbms/SQLSummary.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private static final HashMap SUMMARIES = new HashMap();

    public static SQLSummary get(String text) {
        synchronized (SUMMARIES) {
            SQLSummary result = (SQLSummary) SUMMARIES.get(text);

            if (result == null) {
                result = new SQLSummary(text);
                SUMMARIES.put(text, result);
            }

            return result;
        }
    }

    public static final int SELECT = 0;
    public static final int INSERT = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    public static final int OPAQUE = 4;

    private static final HashMap TYPES = new HashMap();

    static {
        TYPES.put("select", new Integer(SELECT));
        TYPES.put("insert into", new Integer(INSERT));
        TYPES.put("update", new Integer(UPDATE));
        TYPES.put("delete from", new Integer(DELETE));
    }

    private final int m_type;
    private final String[] m_tables;

    private SQLSummary(String text) {
        SQLParser p = new SQLParser(new StringReader(text));
        try {
            p.sql();
        } catch (ParseException e) {
            throw new UncheckedWrapperException(e);
        }
        SQL sql = p.getSQL();
        SQLToken first = sql.getFirst();
        if (first == null) {
            m_type = OPAQUE;
        } else {
            String image = strip(first.getImage().toLowerCase());
            Integer type = (Integer) TYPES.get(image);
            if (type == null) {
                m_type = OPAQUE;
            } else {
                m_type = type.intValue();
            }
        }

        switch (m_type) {
        case INSERT:
        case UPDATE:
        case DELETE:
            SQLToken next = first.getNext();
            if (next == null) {
                m_tables = new String[0];
            } else {
                m_tables =
                    new String[] { strip(next.getImage().toLowerCase()) };
            }
            break;
        default:
            m_tables = new String[0];
        }
    }

    public int getType() {
        return m_type;
    }

    public String[] getTables() {
        return m_tables;
    }

    private static final String strip(String str) {
        str = str.trim();
        StringBuffer result = new StringBuffer(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                char last = result.charAt(result.length() - 1);
                if (last != ' ') {
                    result.append(' ');
                }
                continue;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

}
