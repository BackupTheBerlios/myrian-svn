/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.ProtoException;
import com.redhat.persistence.Session;
import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #8 $ $Date: 2004/09/07 $
 **/

public class Query {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/oql/Query.java#8 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

    private static final Logger s_log = Logger.getLogger(Query.class);

    private Expression m_query;
    private List m_names;
    private Map m_fetched;
    private boolean m_lock;

    public Query(Expression query, boolean lock) {
        m_query = query;
        m_names = new ArrayList();
        m_fetched = new HashMap();
        m_lock = lock;
    }

    public Query(Expression query) {
        this(query, false);
    }

    public void fetch(String name, Expression value) {
        if (m_fetched.containsKey(name)) {
            throw new IllegalArgumentException
                (name + ": already bound to " + m_fetched.get(name));
        }
        m_names.add(name);
        m_fetched.put(name, value);
    }

    private Expression get(String name) {
        return (Expression) m_fetched.get(name);
    }

    public Code generate(Session ssn) {
        return generate(ssn, false);
    }

    public Code generate(Session ssn, boolean oracle) {
        Generator gen = Generator.getThreadGenerator();
        try {
            gen.init(ssn);
            return generate(gen, oracle);
        } catch (Throwable t) {
            ProtoException e =
                new ProtoException("oql compilation error: " + this) {};
            e.initCause(t);
            throw e;
        } finally {
            gen.clear();
        }
    }

    private static final String ROWNUM = "rownum__";

    private static final Map s_cache = new HashMap();

    private Code generate(Generator gen, boolean oracle) {
        m_query.hash(gen);

        for (int i = 0; i < m_names.size(); i++) {
            String name = (String) m_names.get(i);
            gen.hash(name);
            Expression e = get(name);
            e.hash(gen);
        }

        Code cached;

        synchronized (s_cache) {
            cached = (Code) s_cache.get(gen.getLookupKey());
        }

        if (cached != null) {
            Code c = cached.resolve(gen.getBindings(), gen.getRoot());
            return c;
        }

        m_query.frame(gen);

        QFrame qframe = gen.getFrame(m_query);
        gen.push(qframe);
        try {
            for (int i = 0; i < m_names.size(); i++) {
                Expression e = get((String) m_names.get(i));
                e.frame(gen);
            }
        } finally {
            gen.pop();
        }

        check(gen);

        if (s_log.isDebugEnabled()) {
            s_log.debug("unoptimized frame:\n" + qframe);
        }

        List frames = gen.getFrames();

        boolean modified;
        do {
            modified = false;
            for (int i = 0; i < frames.size(); i++) {
                QFrame qf = (QFrame) frames.get(i);
                modified |= qf.hoist();
            }
        } while (modified);

        if (s_log.isDebugEnabled()) {
            s_log.debug("hoisted frame:\n" + qframe);
        }

        for (int i = 0; i < frames.size(); i++) {
            QFrame qf = (QFrame) frames.get(i);
            qf.mergeOuter();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("outers merged:\n" + qframe);
        }

        for (int i = 0; i < frames.size(); i++) {
            QFrame qf = (QFrame) frames.get(i);
            if (qf.getParent() == null) {
                qf.equifill();
            }
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("eq/nn filled:\n" + qframe);
        }

        Set collapse = new HashSet();
        Map canon = new HashMap();
        do {
            collapse.clear();
            canon.clear();
            modified = false;
            for (int i = 0; i < frames.size(); i++) {
                QFrame qf = (QFrame) frames.get(i);
                long st = System.currentTimeMillis();
                modified |= qf.innerize(collapse, canon);
            }
            if (modified) {
                for (Iterator it = collapse.iterator(); it.hasNext(); ) {
                    EquiSet eq = (EquiSet) it.next();
                    eq.collapse();
                }
            }
        } while (modified);

        if (s_log.isDebugEnabled()) {
            s_log.debug("innerized frame:\n" + qframe);
        }

        for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            qf.shrink();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("shrunk frame:\n" + qframe);
        }

        Code sql = new Code("select ");

        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Expression e = get(name);
            // XXX: should eliminate duplicate fetches here when
            // we return something smarter than a string from this
            // method.
            sql = sql.add(e.emit(gen))
                .add(" as \"")
                .add(name)
                .add("\"");
            if (it.hasNext()) {
                sql = sql.add(",\n       ");
            }
        }

        if (m_names.isEmpty()) {
            sql = sql.add("1 as dummy");
        }

        Code from = qframe.emit(false, !oracle);
        if (from == null || from.isEmpty()) {
            if (oracle) {
                sql = sql.add("\nfrom dual");
            }
        } else {
            sql = sql.add("\nfrom ").add(from);
        }

        Expression offset = qframe.getOffset();
        Expression limit = qframe.getLimit();
        if (oracle && (offset != null || limit != null)) {
            // We need one level of nesting here to make sure that the
            // order by happens before the rownum assignments. The
            // second level of nesting is required because filtering
            // on rownum directly doesn't work.
            sql = new Code("select * from (select r__.*, rownum as ")
                .add(ROWNUM).add(" from (").add(sql).add(") r__) where ");
            if (offset != null) {
                sql = sql.add(ROWNUM).add(" > ").add(offset.emit(gen));
            }
            if (limit != null) {
                if (offset != null) {
                    sql = sql.add(" and ");
                }
                sql = sql.add(ROWNUM);
                if (offset != null) {
                    sql = sql.add(" - ").add(offset.emit(gen));
                }
                sql = sql.add(" <= ").add(limit.emit(gen));
            }
        }

        // XXX: need better way to do size
        if (m_query instanceof Size) {
            sql = new Code("select count(*) as \"size\" from (").add(sql)
                .add(") count__");
        }

        if (m_lock) {
            sql = sql.add("\nfor update");
        }

        synchronized (s_cache) {
            s_cache.put(gen.getStoreKey(), sql);
        }

        sql = sql.resolve(gen.getBindings(), gen.getRoot());

        return sql;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("query(");
        result.append(m_query);
        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            result.append(", ");
            result.append(name);
            result.append(" = ");
            result.append(get(name));
        }
        result.append(")");
        return result.toString();
    }

    private void check(Generator gen) {
        List frames = gen.getFrames();
        for (int i = 0; i < frames.size(); i++) {
            QFrame frame = (QFrame) frames.get(i);
            Expression e = frame.getExpression();
            if (gen.isBoolean(e) && e instanceof Define) {
                throw new IllegalStateException
                    ("expecting a boolean expression, not a define: " + e);
            }
        }
    }

}
