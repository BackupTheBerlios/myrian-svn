package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Query
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/02/09 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Query.java#4 $ by $Author: rhs $, $DateTime: 2004/02/09 14:57:03 $";

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
        Code code = new Code(root);
        Code.Frame frame = m_query.frame(code);
        code.push(frame);
        try {
            for (Iterator it = m_names.iterator(); it.hasNext(); ) {
                Expression e = get((String) it.next());
                code.setFrame(e, e.frame(code));
            }
        } finally {
            code.pop();
        }

        code.append("select ");
        if (m_names.isEmpty()) {
            code.append("*");
        } else {
            for (Iterator it = m_names.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                Expression e = get(name);
                code.materialize(e);
                code.append(" as ");
                code.append(name);
                if (it.hasNext()) { code.append(", "); }
            }
        }
        code.append("\nfrom ");
        m_query.emit(code);

        return code.getSQL();
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
