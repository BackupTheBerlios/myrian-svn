package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/02/21 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Query.java#5 $ by $Author: rhs $, $DateTime: 2004/02/21 13:11:19 $";

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
        Generator gen = new Generator(root);
        m_query.frame(gen);

        if (m_names.isEmpty()) {
            return m_query.emit(gen);
        }

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

        s_log.info("unoptimized frame: " + qframe);

        boolean modified;
        do {
            modified = false;
            for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
                QFrame qf = (QFrame) it.next();
                modified |= qf.hoist();
            }
        } while (modified);

        s_log.info("hoisted frame: " + qframe);

        for (Iterator it = gen.getFrames().iterator(); it.hasNext(); ) {
            QFrame qf = (QFrame) it.next();
            qf.shrink();
        }

        s_log.info("shrunk frame: " + qframe);

        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        for (Iterator it = m_names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            Expression e = get(name);
            sql.append(e.emit(gen));
            sql.append(" as ");
            sql.append(name);
            if (it.hasNext()) {
                sql.append(", ");
            }
        }

        sql.append("\nfrom ");
        sql.append(qframe.emit(false));

        return sql.toString();
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
