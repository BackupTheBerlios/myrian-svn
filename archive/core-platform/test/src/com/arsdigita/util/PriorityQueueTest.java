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

import junit.framework.TestCase;
import java.util.NoSuchElementException;

public class PriorityQueueTest extends TestCase {

    public PriorityQueueTest(String name) {
        super(name);
    }

    public void testPriorityQueueAscending() {
        PriorityQueue pq = new PriorityQueue();

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        pq.enqueue("Test 2", 2);
        pq.enqueue("Test 3", 3);
        pq.enqueue("Test 1", 1);

        assert("isEmpty is returned true on an nonempty queue", !pq.isEmpty());

        String peek1 = (String)pq.peek();
        String s1 = (String)pq.dequeue();
        String peek2 = (String)pq.peek();
        String s2 = (String)pq.dequeue();
        String s3 = (String)pq.dequeue();

        assert("size was incorrect (should be 3)", pq.size() != 3);
        assert("isEmpty is returned false on an empty queue", pq.isEmpty());

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        assert("Peek #1 incorrect", peek1.equals("Test 1"));
        assert("Peek #2 incorrect", peek2.equals("Test 2"));
        assert("Dequeue #1 incorrect", s1.equals("Test 1"));
        assert("Dequeue #2 incorrect", s2.equals("Test 2"));
        assert("Dequeue #3 incorrect", s3.equals("Test 3"));
    }

    public void testPriorityQueueDescending() {
        PriorityQueue pq = new PriorityQueue(false);

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        pq.enqueue("Test 2", 2);
        pq.enqueue("Test 1", 3);
        pq.enqueue("Test 3", 1);

        String peek1 = (String)pq.peek();
        String s1 = (String)pq.dequeue();
        String peek2 = (String)pq.peek();
        String s2 = (String)pq.dequeue();
        String s3 = (String)pq.dequeue();

        try {
            pq.dequeue();

            fail("NoSuchElementException not thrown on empty queue");
        } catch (NoSuchElementException e) {
        }

        assert("Peek #1 incorrect", peek1.equals("Test 1"));
        assert("Peek #2 incorrect", peek2.equals("Test 2"));
        assert("Dequeue #1 incorrect", s1.equals("Test 1"));
        assert("Dequeue #2 incorrect", s2.equals("Test 2"));
        assert("Dequeue #3 incorrect", s3.equals("Test 3"));
    }
}
