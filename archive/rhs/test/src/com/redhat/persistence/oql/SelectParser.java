/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.StringReader;

import org.apache.log4j.Logger;

/**
 * SelectParser
 *
 * Simple select statement parser for use in QueryTest tests.
 * Use for counting subselects, inner, and outer joins.
 *
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/05/02 $
 */
public class SelectParser {

    private static final Logger s_log = Logger.getLogger(SelectParser.class);

    private SQLToken m_token = null;
    private SQLToken m_matchend;
    private int m_subselects = 0;
    private int m_inners = 0;
    private int m_outers = 0;

    public SelectParser(final String sqlText) {
        s_log.debug("Parsing: " + sqlText);
        SQL sql = getParsedSQL(sqlText);

        m_token = sql.getFirst();
        while (m_token != null) {
            if (match("(", "select")) {
                m_subselects++;
            } else if (match("left", "join")) {
                m_outers++;
            } else if (match("join")) {
                m_inners++;
            } else {
                m_token = next(m_token);
                continue;
            }
            m_token = m_matchend;
        }
    }

    public int getSubselectCount() {
        return m_subselects;
    }

    public int getJoinCount() {
        return m_inners + m_outers;
    }

    public int getInnerCount() {
        return m_inners;
    }

    public int getOuterCount() {
        return m_outers;
    }

    private boolean match(String t) {
        return match(new String[] { t });
    }

    private boolean match(String t1, String t2) {
        return match(new String[] { t1, t2 });
    }

    private boolean match(String[] images) {
        SQLToken tok = m_token;
        for (int i = 0; i < images.length; i++) {
            if (tok == null) { return false; }
            if (!tok.getImage().equalsIgnoreCase(images[i])) {
                return false;
            }
            tok = next(tok);
        }
        m_matchend = tok;
        return true;
    }

    SQLToken next(SQLToken t) {
        do {
            t = t.getNext();
        } while (t != null && t.isSpace());
        return t;
    }

    private SQL getParsedSQL(final String sql) {
        SQLParser parser = new SQLParser(new StringReader(sql));
        try {
            parser.sql();
        } catch (com.redhat.persistence.common.ParseException e) {
            throw new UncheckedWrapperException(e);
        }

       return parser.getSQL();

    }

}
