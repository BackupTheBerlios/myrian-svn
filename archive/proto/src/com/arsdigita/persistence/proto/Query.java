package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * Query
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2003/03/01 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Query.java#8 $ by $Author: rhs $, $DateTime: 2003/03/01 02:23:27 $";

    private Signature m_signature;
    private Filter m_filter;
    private ArrayList m_order = new ArrayList();
    private HashSet m_ascending = new HashSet();
    private HashMap m_values;

    public Query(Signature signature, Filter filter) {
        m_signature = signature;
        m_filter = filter;
        m_values = new HashMap(m_signature.getParameters().size());
    }

    public Query(Query query, Filter filter) {
        this(query.getSignature(),
             query.getFilter() == null ?
             filter : new AndFilter(query.getFilter(), filter));
        for (Iterator it = getSignature().getParameters().iterator();
             it.hasNext(); ) {
            Parameter p = (Parameter) it.next();
            set(p, query.get(p));
        }

        m_order.addAll(query.m_order);
        m_ascending.addAll(query.m_ascending);
    }

    public Signature getSignature() {
        return m_signature;
    }

    public Filter getFilter() {
        return m_filter;
    }

    public void addOrder(Path path, boolean isAscending) {
        if (m_order.contains(path)) {
            throw new IllegalArgumentException
                ("already ordered by path: " + path);
        }

        m_order.add(path);
        if (isAscending) {
            m_ascending.add(path);
        }
    }

    public Collection getOrder() {
        return m_order;
    }

    public boolean isAscending(Path p) {
        return m_ascending.contains(p);
    }

    public void clearOrder() {
        m_order.clear();
        m_ascending.clear();
    }

    public void set(Parameter p, Object value) {
        m_values.put(p, value);
    }

    public Object get(Parameter p) {
        return m_values.get(p);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(m_signature + "\nfilter(" + m_filter + ")\norder(");
        for (Iterator it = m_order.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            buf.append(p);
            if (!isAscending(p)) {
                buf.append(" desc");
            }
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append(")");
        buf.append(m_values);
        return buf.toString();
    }

}
