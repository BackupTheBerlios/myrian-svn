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

package com.arsdigita.initializer;

import junit.framework.*;
import java.io.*;
import java.util.*;

/**
 * ScriptTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class ScriptTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/initializer/ScriptTest.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public ScriptTest(String name) {
        super(name);
    }

    public void testParser() {
        try {
            Script s = new Script(
                                  "init com.arsdigita.initializer.FooInitializer {\n" +
                                  "  stringParam = \"string\";\n" +
                                  "  objectParam = 3;\n" +
                                  "  listParam = {1, 2, 3, {4, 5, 6}};\n" +
                                  "}"
                                  );

            List inis = s.getInitializers();
            assertEquals("There should be exactly one " +
                         "initializer in the script.",
                         1, inis.size());
            for (int i = 0; i < inis.size(); i++) {
                Initializer ini = (Initializer) inis.get(i);
                Configuration conf = ini.getConfiguration();
                assertEquals("stringParam wasn't set properly",
                             "string", conf.getParameter("stringParam"));
                assertEquals("objectParam wasn't set properly",
                             new Integer(3),
                             conf.getParameter("objectParam"));
                List l = new ArrayList();
                l.add(new Integer(1));
                l.add(new Integer(2));
                l.add(new Integer(3));
                List subl = new ArrayList();
                l.add(subl);
                subl.add(new Integer(4));
                subl.add(new Integer(5));
                subl.add(new Integer(6));
                assertEquals("listParam wasn't set properly",
                             l, conf.getParameter("listParam"));
            }
        } catch (InitializationException e) {
            fail(e.getMessage());
        }
    }

    public void testStartupAndShutdown() {
        try {
            Script s = new Script(
                                  "init com.arsdigita.initializer.FooInitializer {}"
                                  );

            assertTrue("FooInitializer should start out as not started",
                   !FooInitializer.isStarted());

            s.startup();

            assertTrue("FooInitializer wasn't started",
                   FooInitializer.isStarted());

            s.shutdown();

            assertTrue("FooInitializer wasn't shutdown",
                   !FooInitializer.isStarted());
        } catch (InitializationException e) {
            fail(e.getMessage());
        }
    }

}
