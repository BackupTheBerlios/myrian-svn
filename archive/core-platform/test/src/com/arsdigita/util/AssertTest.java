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

import junit.framework.TestCase;

public class AssertTest extends TestCase {
    public static final String versionId =
        "$Id: //core-platform/dev/test/src/com/arsdigita/util/AssertTest.java#5 $" +
        "$Author: vadim $" +
        "$DateTime: 2003/05/21 16:45:33 $";

    public AssertTest(String name) {
        super(name);
    }

    public void testAssert() {
        Util.getConfig().setAssertEnabled(false);

        junit.framework.Assert.assertTrue(!Assert.isEnabled());

        Util.getConfig().setAssertEnabled(true);

        junit.framework.Assert.assertTrue(Assert.isEnabled());

        try {
            com.arsdigita.util.Assert.truth(false, "Expected true");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.falsity(true, "Expected false");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.exists(null, Object.class);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.locked(new Unlocked());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unlocked(new Locked());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(new Object(), new Object());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal("whoa", "dude");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(null, new Object());

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.equal(new Object(), null);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            final Object one = new Object();

            com.arsdigita.util.Assert.unequal(one, one);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unequal(null, null);

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        try {
            com.arsdigita.util.Assert.unequal("dude", "dude");

            junit.framework.Assert.fail();
        } catch (AssertionError e) {
            // Empty
        }

        // Tests for the deprecated assert methods

        try {
            com.arsdigita.util.Assert.assertTrue(false);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertTrue(false, "Is false!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null, "Is null!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null);
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null, "NullString");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("", "emptyString!");
            junit.framework.Assert.fail();
        } catch (IllegalStateException e) {
        }
    }

    private class Locked extends LockableImpl {
        Locked() {
            lock();
        }
    }

    private class Unlocked extends LockableImpl {
        // Empty
    }
}
