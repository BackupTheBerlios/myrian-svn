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
import java.util.Map;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * An implementation of Map which preserves the order in which you put
 * entries into it.
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 */
public class OrderedMap extends TreeMap {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/OrderedMap.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2002/08/14 23:39:40 $";

    private static final Logger s_log = Logger.getLogger
        (OrderedMap.class.getName());

    private OrderedComparator m_comparator;

    public OrderedMap() {
        super(new OrderedComparator());

        m_comparator = (OrderedComparator) comparator();
    }

    /**
     * Calls to put define the order in which the OrderedMap returns
     * its contents in calls to entrySet().iterator();
     */
    public Object put(Object key, Object value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding a new map entry: " + key + " => " + value);
        }

        m_comparator.keep(key);

        return super.put(key, value);
    }
}

class OrderedComparator implements Comparator {
    private HashMap m_sortKeyMap = new HashMap();
    private long m_currSortKey = 0;

    public int compare(Object o1, Object o2) {
        Long sk1 = (Long) m_sortKeyMap.get(o1);
        Long sk2 = (Long) m_sortKeyMap.get(o2);

        if (sk1 == null) return 1;
        if (sk2 == null) return -1;

        return (int) (sk1.longValue() - sk2.longValue());
    }

    public void keep(Object key) {
        m_sortKeyMap.put(key, new Long(m_currSortKey++));
    }
}
