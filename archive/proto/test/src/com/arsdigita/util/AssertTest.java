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

    public static final String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/util/AssertTest.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public AssertTest(String name) {
        super(name);
    }

    public void testAssert() {

        try {
            com.arsdigita.util.Assert.assertTrue(false);
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertTrue(false, "Is false!");
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null);
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotNull(null, "Is null!");
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null);
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("");
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty(null, "NullString");
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }

        try {
            com.arsdigita.util.Assert.assertNotEmpty("", "emptyString!");
            junit.framework.Assert.assertTrue(false);
        } catch (IllegalStateException e) {
        }
    }
}
