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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class SQLSummary {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/profiler/rdbms/SQLSummary.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
