package com.arsdigita.persistence.proto;

import java.util.*;

/**
 * Query
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/12/04 $
 **/

public class Query {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Query.java#2 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private Signature m_signature;
    private Filter m_filter;
    private ArrayList m_orders = new ArrayList();

    public Query(Signature signature, Filter filter) {
        m_signature = signature;
        m_filter = filter;
    }

    public Signature getSignature() {
        return m_signature;
    }

    public Filter getFilter() {
        return m_filter;
    }

    public Collection getOrders() {
        return m_orders;
    }

    public void addOrder(Order order) {
        m_orders.add(order);
    }

    public String toString() {
        return m_signature + "\n" + m_filter + "\n" + m_orders;
    }

}
