package com.arsdigita.persistence.proto;

import java.util.*;

/**
 * Query
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/01/06 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Query.java#5 $ by $Author: rhs $, $DateTime: 2003/01/06 17:58:56 $";

    private Signature m_signature;
    private Filter m_filter;
    private ArrayList m_orders = new ArrayList();
    private HashMap m_values;

    public Query(Signature signature, Filter filter) {
        m_signature = signature;
        m_filter = filter;
        m_values = new HashMap(m_signature.getParameters().size());
    }

    public Signature getSignature() {
        return m_signature;
    }

    public Filter getFilter() {
        return m_filter;
    }

    public void addOrder(Order order) {
        m_orders.add(order);
    }

    public Collection getOrders() {
        return m_orders;
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
        for (Iterator it = m_orders.iterator(); it.hasNext(); ) {
            buf.append(it.next());
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
        buf.append(")");
        buf.append(m_values);
        return buf.toString();
    }

}
