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
 * The static methods in this class provide a standard way of
 * asserting certain conditions.
 *
 * Though it is not right now, this class <em>should</em> be final.
 * Do not subclass it.  In a future revision of this software, this
 * class will be made final.
 *
 * @author David Lutterkort
 * @author Uday Mathur
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/Assert.java#13 $
 */
public class Assert {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/Assert.java#13 $" +
        "$Author: justin $" +
        "$DateTime: 2003/04/26 19:14:38 $";

    private static final Logger s_log = Logger.getLogger
        (Assert.class);

    /**
     * Tells whether asserts are turned on.  Use this to wrap code
     * that should be optimized away if assertions are disabled.
     */
    public static final boolean isEnabled() {
        // Making this a little less costly will be good. XXX

        return Util.getConfig().isAssertEnabled();
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws java.lang.Error if the condition is false
     */
    public static final void truth(final boolean condition,
                                   final String message) {
        if (!condition) {
            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Asserts that an arbitrary condition is true and throws an
     * error with message <code>message</code> if the condition is
     * false.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws java.lang.Error if the condition is false
     */
    public static final void truth(final boolean condition) {
        if (!condition) {
            final String message = "Assertion failure";

            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Asserts that an arbitrary condition is false and throws an
     * error if the condition is true.
     *
     * @param condition The condition asserted
     * @param message An error message
     * @throws java.lang.Error if the condition is false
     */
    public static final void falsity(final boolean condition,
                                     final String message) {
        if (condition) {
            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Asserts that an object is not null.
     *
     * @param object The object that must not be null
     * @param clacc The <code>Class</code> of parameter
     * <code>object</code>
     * @throws java.lang.Error if the object is null
     */
    public static final void exists(final Object object,
                                    final Class clacc) {
        if (object == null) {
            final String message = clacc.getName() + " is null";

            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Verifies that <code>Lockable</code> is locked and throws an
     * error if it is not.
     *
     * @param lockable The object that must be locked
     * @see com.arsdigita.util.Lockable
     */
    public static final void locked(final Lockable lockable) {
        if (lockable != null && !lockable.isLocked()) {
            final String message = lockable + " is not locked";

            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Verifies that <code>lockable</code> is <em>not</em> locked and
     * throws an error if it is.
     *
     * @param lockable The object that must not be locked
     * @see com.arsdigita.util.Lockable
     */
    public static final void unlocked(final Lockable lockable) {
        if (lockable != null && lockable.isLocked()) {
            final String message = lockable + " is locked";

            s_log.error(message);

            throw new Error(message);
        }
    }

    /**
     * Verifies that two values are equal (according to their equals
     * method, unless <code>value1</code> is null, then according to
     * <code>==</code>).
     *
     * @param value1 The first value to be compared
     * @param value2 The second
     * @throws java.lang.Error if the arguments are unequal
     */
    public static final void equal(final Object value1,
                                   final Object value2) {
        if (value1 == null) {
            if (value1 != value2) {
                final String message = value1 + " does not equal " + value2;

                s_log.error(message);

                throw new Error(message);
            }
        } else {
            if (!value1.equals(value2)) {
                final String message = value1 + " does not equal " + value2;

                s_log.error(message);

                throw new Error(message);
            }
        }
    }

    /**
     * Verifies that two values are not equal (according to their
     * equals method, unless <code>value1</code> is null, then
     * according to <code>==</code>).
     *
     * @param value1 The first value to be compared
     * @param value2 The second
     * @throws java.lang.Error if the arguments are unequal
     */
    public static final void unequal(final Object value1,
                                     final Object value2) {
        if (value1 == null) {
            if (value1 == value2) {
                final String message = value1 + " equals " + value2;

                s_log.error(message);

                throw new Error(message);
            }
        } else {
            if (value1.equals(value2)) {
                final String message = value1 + " equals " + value2;

                s_log.error(message);

                throw new Error(message);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    // The methods below are all deprecated.                                 //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @deprecated in favor of {@link #isEnabled()}
     */
    public static final boolean ASSERT_ON = true;

    /**
     * Indicates state of the ASSERT_ON flag.
     *
     * @deprecated Use {@link #isEnabled()} instead
     */
    public static final boolean isAssertOn() {
        return isEnabled();
    }

    /**
     * Tells whether asserts are turned on.  Use this to wrap code
     * that should be optimized away if asserts are disabled.
     *
     * @deprecated Use {@link #isEnabled()} instead
     */
    public static final boolean isAssertEnabled() {
        return isEnabled();
    }

    /**
     * Assert that an arbitrary condition is true, and throw an
     * exception if the condition is false.
     *
     * @param cond condition to assert
     * @throws java.lang.IllegalStateException condition was false
     * @deprecated Use {@link #truth(boolean, String)} instead
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
     * @deprecated Use {@link #truth(boolean,String)} instead
     */
    public static final void assertTrue(boolean cond, String msg) {
        if (!cond) {
            s_log.error(msg);

            throw new IllegalStateException(msg);
        }
    }

    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     *
     * @deprecated Use {@link #exists(Object,Class)} instead
     */
    public static final void assertNotNull(Object o) {
        assertNotNull(o, "");
    }

    /**
     * Verify that a parameter is not null and throw a runtime
     * exception if so.
     *
     * @deprecated Use {@link #exists(Object,Class)} instead
     */
    public static final void assertNotNull(Object o, String label) {
        if (isEnabled()) {
            assertTrue(o != null, "Value of " + label + " is null.");
        }
    }

    /**
     * Verify that a string is not empty and throw a runtime exception
     * if so.  A parameter is considered empty if it is null, or if it
     * does not contain any characters that are non-whitespace.
     *
     * @deprecated with no replacement
     */
    public static final void assertNotEmpty(String s) {
        assertNotEmpty(s, "");
    }

    /**
     * Verify that a string is not empty and throw a runtime exception
     * if so.  A parameter is considered empty if it is null, or if it
     * does not contain any characters that are non-whitespace.
     *
     * @deprecated with no replacement
     */
    public static final void assertNotEmpty(String s, String label) {
        if (isEnabled()) {
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
     * @deprecated Use {@link #equal(Object,Object)} instead
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
        if (isEnabled()) {
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
     * @deprecated Use {@link #truth(boolean, String)} instead
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
     * @deprecated Use {@link #truth(boolean, String)} instead
     */
    public static final void assertEquals(int expected, int actual,
                                          String expectedLabel,
                                          String actualLabel) {
        if (isEnabled()) {
            assertTrue(expected == actual,
                       "Values not equal, " +
                       expectedLabel + " '" + expected + "', " +
                       actualLabel + " '" + actual + "'");
        }
    }

    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is not locked.
     *
     * @deprecated Use {@link #locked(Lockable)} instead
     */
    public static void assertLocked(Lockable l) {
        if (isEnabled()) {
            assertTrue(l.isLocked(),
                       "Illegal access to an unlocked " +
                       l.getClass().getName());
        }
    }

    /**
     * Verify that the model is locked and throw a runtime exception
     * if it is locked.
     *
     * @deprecated Use {@link #unlocked(Lockable)} instead
     */
    public static void assertNotLocked(Lockable l) {
        if (isEnabled()) {
            assertTrue (!l.isLocked(),
                        "Illegal access to a locked " +
                        l.getClass().getName());
        }
    }

    /**
     * This is the equivalent of assertTrue(false, msg).
     *
     * @param msg A string describing the condition of failure.
     * @throws java.lang.Error
     * @deprecated because it's just as simple to throw an
     * <code>Error</code> or <code>Exception</code>
     */
    public static void fail(String msg) {
        assertTrue(false, msg);
    }

    /**
     * This is the equivalent of assertTrue(false).
     *
     * @deprecated because it's just as simple to throw an
     * <code>Error</code> or <code>Exception</code>
     */
    public static void fail() {
        assertTrue(false);
    }
}
