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

import org.apache.log4j.Logger;

/**
 * Utility functions for assertions.
 *
 * The static methods in this class provide a standard way of asserting
 * certain conditions.
 *
 * @author David Lutterkort (lutter@arsdigita.com)
 * @author Uday Mathur (umathur@arsdigita.com)
 * @version $Id: //core-platform/proto/src/com/arsdigita/util/Assert.java#1 $
 *
 */
public class Assert {
    public static final String versionId =
        "$Id: //core-platform/proto/src/com/arsdigita/util/Assert.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2002/11/27 19:51:05 $";

    private static final Logger s_log = Logger.getLogger
        (Assert.class);

    public static final boolean ASSERT_ON = true;

    /**
     * Indicates state of the ASSERT_ON flag.
     *
     * @deprecated Use {#isAssertEnabled()} instead.
     */
    public static final boolean isAssertOn() {
        return isAssertEnabled();
    }

    /**
     * Tells whether asserts are turned on.  Use this to wrap code
     * that should be optimized away if asserts are disabled.
     */
    public static final boolean isAssertEnabled() {
        return ASSERT_ON;
    }

    /**
     * Assert that an arbitrary condition is true, and throw an
     * exception if the condition is false.
     *
     * @param cond condition to assert
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertTrue(boolean cond) {
        assertTrue(cond, "");
    }

    /**
     * Assert that an arbitrary condition is true, and throw an
     * exception with message <code>msg</code> if the condition is
     * false.
     *
     * @param cond condition to assert
     * @param msg failure message
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertTrue(boolean cond, String msg) {
        if (ASSERT_ON && !cond) {
            throw new IllegalStateException("Assertion failed: " + msg);
        }
    }

    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     */
    public static final void assertNotNull(Object o) {
        assertNotNull(o, "");
    }

    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     */
    public static final void assertNotNull(Object o, String label) {
        if (ASSERT_ON) {
            assertTrue(o != null, "Value of " + label + " is null.");
        }
    }

    /**
     * Verify that a string is not empty and throw a runtime exception
     * if so.  A parameter is considered empty if it is null, or if it
     * does not contain any characters that are non-whitespace.
     */
    public static final void assertNotEmpty(String s) {
        assertNotEmpty(s, "");
    }

    /**
     * Verify that a string is not empty and throw a runtime exception
     * if so.  A parameter is considered empty if it is null, or if it
     * does not contain any characters that are non-whitespace.
     */
    public static final void assertNotEmpty(String s, String label) {
        if (ASSERT_ON) {
            assertTrue(s != null && s.trim().length() > 0,
                       "Value of " + label + " is empty.");
        }
    }

    /**
     * Verify that two values are equal (according to their equals method,
     * unless expected is null, then according to ==).
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "expected", "actual");
    }

    /**
     * Verify that two values are equal (according to their equals method,
     * unless expected is null, then according to ==).
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @param expectedLabel Label for first (generally expected) value.
     * @param actualLabel Label for second (generally actual) value.
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertEquals(Object expected, Object actual,
                                          String expectedLabel,
                                          String actualLabel) {
        if (ASSERT_ON) {
            if (expected == null) {
                assertTrue(expected == actual,
                           "Values not equal, " +
                           expectedLabel + " '" + expected + "', " +
                           actualLabel + " '" + actual + "'");
            } else {
                assertTrue(expected.equals(actual),
                           "Values not equal, " +
                           expectedLabel + " '" + expected + "', " +
                           actualLabel + " '" + actual + "'");
            }
        }
    }

    /**
     * Verify that two values are equal.
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertEquals(int expected, int actual) {
        assertEquals(expected, actual, "expected", "actual");
    }

    /**
     * Verify that two values are equal.
     *
     * @param expected Expected value.
     * @param actual Actual value.
     * @param expectedLabel Label for first (generally expected) value.
     * @param actualLabel Label for second (generally actual) value.
     * @throws java.lang.IllegalStateException condition was false
     */
    public static final void assertEquals(int expected, int actual,
                                          String expectedLabel,
                                          String actualLabel) {
        if (ASSERT_ON) {
            assertTrue(expected == actual,
                       "Values not equal, " +
                       expectedLabel + " '" + expected + "', " +
                       actualLabel + " '" + actual + "'");
        }
    }

    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is not locked.
     */
    public static void assertLocked(Lockable l) {
        assertTrue(l.isLocked(),
                   "Illegal access to an unlocked " + l.getClass().getName());
    }

    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is locked.
     */
    public static void assertNotLocked(Lockable l) {
        assertTrue (!l.isLocked(),
                    "Illegal access to a locked " + l.getClass().getName());
    }


    /**
     * This is the equivalent of assertTrue(false, msg).
     *
     * @param msg A string describing the condition of failure.
     */
    public static void fail(String msg) {
        assertTrue(false, msg);
    }
}