/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.util;

import java.util.*;
import junit.framework.*;

/**
 * @author Jim Parsons
 * @author Justin Ross
 * @version $Id: //core-platform/dev/test/src/com/arsdigita/util/OrderedMapTest.java#4 $
 */
public class OrderedMapTest extends TestCase {
    public void testOrderedMap() {
        final OrderedMap map = new OrderedMap();

        map.put(new Integer(1), "one");
        map.put(new Integer(2), "two");
        map.put(new Integer(3), new String("three"));
        map.put(new Integer(1), new String("one"));
        map.put(new Integer(3), new String("four"));

        final Iterator iter = map.values().iterator();

        while (iter.hasNext()) {
            System.out.println((String) iter.next());
        }
    }
}
