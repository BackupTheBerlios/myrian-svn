/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util;

import java.util.TreeMap;
import java.util.HashMap;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * An implementation of Map which preserves the order in which you put
 * entries into it.
 *
 * @deprecated use {@link com.arsdigita.util.SequentialMap} instead
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class OrderedMap extends TreeMap {
    public static final String versionId =
        "$Id: //core-platform/proto/src/com/arsdigita/util/OrderedMap.java#4 $" +
        "$Author: ashah $" +
        "$DateTime: 2003/04/24 15:32:09 $";

    private static final Logger s_log = Logger.getLogger(OrderedMap.class);

    private OrderingComparator m_comparator;

    public OrderedMap() {
        super(new OrderingComparator());

        m_comparator = (OrderingComparator) comparator();
    }

    /**
     * Calls to put define the order in which the OrderedMap returns
     * its contents in calls to entrySet().iterator();
     */
    public Object put(final Object key, final Object value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding a new map entry: " + key + " => " + value);
        }

        m_comparator.keep(key);

        return super.put(key, value);
    }

    public Object clone() {
        final OrderedMap result = (OrderedMap) super.clone();

        result.m_comparator = (OrderingComparator) m_comparator.clone();

        return result;
    }

    public void clear() {
        super.clear();

        m_comparator.clear();
    }
}

final class OrderingComparator implements Comparator, Cloneable {
    private HashMap m_sortKeyMap = new HashMap();
    private long m_currSortKey = 0;

    public final int compare(final Object o1, final Object o2) {
        Long sk1 = (Long) m_sortKeyMap.get(o1);
        Long sk2 = (Long) m_sortKeyMap.get(o2);

        if (sk1 == null) return 1;
        if (sk2 == null) return -1;

        return (int) (sk1.longValue() - sk2.longValue());
    }

    final void keep(final Object key) {
        m_sortKeyMap.put(key, new Long(m_currSortKey++));
    }

    protected Object clone() {
        try {
            final OrderingComparator result =
                (OrderingComparator) super.clone();

            result.m_sortKeyMap = (HashMap) m_sortKeyMap.clone();

            return result;
        } catch (CloneNotSupportedException cnse) {
            // I don't think we can get here.

            return null;
        }
    }

    final void clear() {
        m_sortKeyMap.clear();
        m_currSortKey = 0;
    }
}
