package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #15 $ $Date: 2004/03/18 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Query.java#15 $ by $Author: rhs $, $DateTime: 2004/03/18 17:18:33 $";

    private static final Logger s_log = Logger.getLogger(Query.class);

    private Expression m_query;
    private List m_names;
    private Map m_fetched;

    public Query(Expression query) {
        m_query = query;
        m_names = new ArrayList();
        m_fetched = new HashMap();
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

    public Code generate(Root root) {
        return generate(root, false);
    }

    private static final String ROWNUM = "rownum__";

    public Code generate(Root root, boolean oracle) {
        Generator gen = new Generator(root);
        m_query.frame(gen);

        QFrame qframe = gen.getFrame(m_query);
        gen.push(qframe);
        try {
            for (Iterator it = m_names.iterator(); it.hasNext(); ) {
                Expression e = get((String) it.next());
                e.frame(gen);
            }
        } finally {
            gen.pop();
        }

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

        for (int i = 0; i < frames.size(); i++) {
            QFrame qf = (QFrame) frames.get(i);
            if (qf.getParent() == null) {
                qf.equifill();
            }
        }

        do {
            modified = false;
            Set collapse = new HashSet();
            for (int i = 0; i < frames.size(); i++) {
                QFrame qf = (QFrame) frames.get(i);
                long st = System.currentTimeMillis();
                modified |= qf.innerize(collapse);
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
            sql = sql.add("1");
        }

        sql = sql.add("\nfrom ").add(qframe.emit(false, !oracle));

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

}
