package com.arsdigita.persistence.proto;

import java.util.*;

/**
 * Query
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/01/06 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Query.java#4 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private Signature m_signature;

    private ArrayList m_sources = new ArrayList();
    private HashMap m_sourceMap = new HashMap();

    private ArrayList m_parameters = new ArrayList();
    private HashMap m_parameterMap = new HashMap();

    private Filter m_filter = null;
    private ArrayList m_orders = new ArrayList();

    public Query(Signature signature) {
        m_signature = signature;
    }

    public Signature getSignature() {
        return m_signature;
    }

    public void addSource(Source s) {
        if (s == null) {
            throw new IllegalArgumentException
                ("Cannot add a null source.");
        }

        if (m_sourceMap.containsKey(s.getPath())) {
            throw new IllegalArgumentException
                ("Query already contains a source for that path: " +
                 s.getPath());
        }

        if (m_parameterMap.containsKey(s.getPath())) {
            throw new IllegalArgumentException
                ("Query contains a parameter with that path: " +
                 s.getPath());
        }

        m_sources.add(s);
        m_sourceMap.put(s.getPath(), s);
    }

    public Source getSource(Path p) {
        return (Source) m_sourceMap.get(p);
    }

    public Collection getSources() {
        return m_sources;
    }

    public void addParameter(Parameter p) {
        if (p == null) {
            throw new IllegalArgumentException
                ("Cannot add a null parameter.");
        }

        if (m_parameterMap.containsKey(p.getPath())) {
            throw new IllegalArgumentException
                ("Query already contains a parameter for that path: " +
                 p.getPath());
        }

        if (m_sourceMap.containsKey(p.getPath())) {
            throw new IllegalArgumentException
                ("Query contains a source with that path: " + p.getPath());
        }

        m_parameters.add(p);
        m_parameterMap.put(p.getPath(), p);
    }

    public Parameter getParameter(Path p) {
        return (Parameter) m_parameterMap.get(p);
    }

    public Collection getParameters() {
        return m_parameters;
    }

    public void setFilter(Filter filter) {
        m_filter = filter;
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
        return buf.toString();
    }

}
