/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.oql;

import com.redhat.persistence.common.*;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.StringReader;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.log4j.Logger;

/**
 * SelectParser
 *
 * Simple select statement parser for use in QueryTest tests.
 * Use for counting subselects & joins.
 *
 * Caveats in this version:
 *    Only supports left join
 *    Doesn't count exist clauses yet.
 *    Doesn't check for duplicate tables in select statement.
 *
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/02/24 $
 */
public class SelectParser {

    SQLToken m_token = null;
    String m_select;
    String m_from;
    String m_where;
    List m_subselects = new ArrayList();
    private List m_leftJoins = new ArrayList();

    private static final Logger s_log = Logger.getLogger(SelectParser.class);
    public SelectParser(final String sqlText) {

        s_log.debug("Parsing: " + sqlText);
        try {
            SQL sql = getParsedSQL(sqlText);

            m_token = sql.getFirst();
            m_select = m_token.getImage();

            m_token = m_token.getNext();
            while(m_token != null) {
                final String image = m_token.getImage().trim();
                //System.out.println("Image: " + image);
                if (image.startsWith("from")) {
                    getFromClause();
                    if (atWhereClause()) {
                        getWhereClause();
                    } else if(atLeftJoinClause()) {
                        getLeftJoinClause();
                    }

                } else {
                    m_select += image;
                    m_select += " ";
                    m_token = m_token.getNext();
                }
            }
        } catch (Exception e) {
            String tokenized = debugTokenize(sqlText);
            throw new UncheckedWrapperException("Error parsing " + sqlText +" Debug tokenized is: \n" + tokenized, e);
        }
    }

    private void getLeftJoinClause() {
        Assert.truth(atLeftJoinClause(), "Can't get join clause when not at that point!");
        StringBuffer leftJoin = new StringBuffer();
        leftJoin.append(m_token.getImage().trim());
        leftJoin.append(" ");

        m_token = m_token.getNext();
        while(m_token != null) {
            if (atLeftJoinClause()) {
                m_leftJoins.add(leftJoin);
                leftJoin = new StringBuffer();
            }
            leftJoin.append(m_token.getImage());
            leftJoin.append(" ");
            m_token = m_token.getNext();
        }
    }

    public String getSelect() {
        return m_select;
    }

    public String getWhere() {
        return m_where;
    }

    public String getFrom() {
        return m_from;
    }

    public int getSubselectCount() {
        return m_subselects.size();
    }

    public List getSubselects() {
        return Collections.unmodifiableList(m_subselects);
    }

    public int getJoinCount() {
        return m_leftJoins.size();
    }


    public List getJoins() {
        return Collections.unmodifiableList(m_leftJoins);
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

    private void getFromClause() {
        boolean done = false;

        StringBuffer fromClause = new StringBuffer();
        while(m_token != null & !done) {
            final String sql = m_token.getImage().trim();
            done = atWhereClause() || atLeftJoinClause();
            if(!done) {

                if (hasSubselect(sql)) {
                    //System.out.println("Getting subselect: ");
                    String subSelect = getSubSelect();
                    m_subselects.add(subSelect);
                    //System.out.println("Subselect is: " + subSelect);
                    fromClause.append(subSelect);
                    fromClause.append(",");
                } else {
                    if(!sql.equals("from")) {
                        fromClause.append(sql);
                        fromClause.append(" ");
                    }
                }
                m_token = m_token.getNext();
            }
        }
        //System.out.println("Done with from clause");
        m_from = fromClause.toString();
        if (m_from.endsWith(",")) {
            m_from = m_from.substring(0, m_from.length() - 2);
        }
    }

    private void getWhereClause() {
        Assert.truth(atWhereClause(), "Can't get where clause when not at that point!");
        StringBuffer where = new StringBuffer();
        String begin = m_token.getImage().trim();
        if (begin.equals("where")) {
            begin = "";
        } else {
            begin = begin.substring(5);
        }
        where.append(begin);
        m_token = m_token.getNext();
        while(m_token != null) {
            String fragment = m_token.getImage();
            where.append(fragment);
            where.append(" ");
            m_token = m_token.getNext();
        }
        m_where = where.toString();

    }

    private String getSubSelect() {
        final String sql = m_token.getImage();
        String start = sql.substring(sql.indexOf("select")).trim();
        StringBuffer subSelect = new StringBuffer();
        subSelect.append("(");
        subSelect.append(start);
        m_token = m_token.getNext();
        boolean done = false;
        while(m_token != null && !done) {
            String fragment = m_token.getImage().trim();
            done = fragment.equals(")");
            subSelect.append(fragment);
            subSelect.append(" ");
            m_token = m_token.getNext();
        }

        return subSelect.toString();
    }

    private boolean hasSubselect(String sqlFragment) {
        Perl5Util re = new Perl5Util();
//        System.out.println("Subselect test: " + sqlFragment);
        return re.match("/\\(\\s?select/", sqlFragment);

    }


    private String debugTokenize(String original) {
        SQL sql = getParsedSQL(original);

        SQLToken token = sql.getFirst();
        StringBuffer dbg = new StringBuffer();

        while(token != null) {
            final String image = token.getImage().trim();
            String msg = "Token ";
            if (token.isPath()) {
                msg += "is path ";
            }
            msg += ": ";

            msg += image;
            dbg.append(msg);
            dbg.append("\n");
            token = token.getNext();
        }
        return dbg.toString();
    }


    private boolean atWhereClause() {
        return m_token != null && m_token.getImage().trim().startsWith("where");
    }

    private boolean atLeftJoinClause() {
        return m_token != null && m_token.getImage().trim().startsWith("left") && m_token.isPath();
    }

}
