/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
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
import org.apache.log4j.Category;

/**
 * An implementation of Map which preserves the order in which you put
 * entries into it.
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 */
public class OrderedMap extends TreeMap {
    public static final String versionId = 
        "$Id: //core-platform/dev/src/com/arsdigita/util/OrderedMap.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2002/08/01 14:48:43 $";

    private static Category s_log = Category.getInstance
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

        m_comparator.keep(value);

        return super.put(key, value);
    }
}

class OrderedComparator implements Comparator {
    private HashMap m_sortKeyMap = new HashMap();
    private long m_currSortKey = 0;
    
    public int compare(Object o1, Object o2) {
        long sk1 = ((Long) m_sortKeyMap.get(o1)).longValue();
        long sk2 = ((Long) m_sortKeyMap.get(o2)).longValue();
        
        return (int) (sk1 - sk2);
    }
    
    public void keep(Object value) {
        m_sortKeyMap.put(value, new Long(m_currSortKey++));
    }
}

