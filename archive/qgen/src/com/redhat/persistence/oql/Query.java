package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #12 $ $Date: 2004/03/08 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Query.java#12 $ by $Author: rhs $, $DateTime: 2004/03/08 23:10:10 $";

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

    public String generate(Root root) {
        return generate(root, false);
    }

    private static final String ROWNUM = "rownum__";

    public String generate(Root root, boolean oracle) {
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

        boolean modified;
        do {
            modified = false;
            for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
                QFrame qf = (QFrame) it.next();
                modified |= qf.hoist();
            }
        } while (modified);

        if (s_log.isDebugEnabled()) {
            s_log.debug("hoisted frame:\n" + qframe);
        }

        for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            if (qf.getParent() == null) {
                EquiSet eq = new EquiSet(gen);
                gen.equateAll(eq, qf);
                eq.collapse();
                qf.setEquiSet(eq);
            }
        }

        do {
            modified = false;
            for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
                QFrame qf = (QFrame) it.next();
                modified |= qf.innerize();
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

        StringBuffer sql = new StringBuffer();
        sql.append("select ");

        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Expression e = get(name);
            // XXX: should eliminate duplicate fetches here when
            // we return something smarter than a string from this
            // method.
            sql.append(e.emit(gen));
            sql.append(" as \"");
            sql.append(name);
            sql.append("\"");
            if (it.hasNext()) {
                sql.append(",\n       ");
            }
        }

        if (m_names.isEmpty()) {
            sql.append("1");
        }

        sql.append("\nfrom ");
        sql.append(qframe.emit(false, !oracle));

        Expression offset = qframe.getOffset();
        Expression limit = qframe.getLimit();
        if (oracle && (offset != null || limit != null)) {
            // We need one level of nesting here to make sure that the
            // order by happens before the rownum assignments. The
            // second level of nesting is required because filtering
            // on rownum directly doesn't work.
            sql.insert(0, "select * from (select r__.*, rownum as " +
                       ROWNUM + " from (");
            sql.append(") r__) where ");
            if (offset != null) {
                sql.append(ROWNUM);
                sql.append(" > ");
                sql.append(offset.emit(gen));
            }
            if (limit != null) {
                if (offset != null) {
                    sql.append(" and ");
                }
                sql.append(ROWNUM);
                if (offset != null) {
                    sql.append(" - ");
                    sql.append(offset.emit(gen));
                }
                sql.append(" <= " + limit.emit(gen));
            }
        }

        // XXX: need better way to do size
        if (m_query instanceof Size) {
            sql.insert(0, "select count(*) as \"size\" from (");
            sql.append(") count__");
        }

        String result = sql.toString();

        return result;
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
