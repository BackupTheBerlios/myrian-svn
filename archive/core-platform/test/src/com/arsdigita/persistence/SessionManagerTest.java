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

package com.arsdigita.persistence;

import junit.framework.TestCase;

/**
 * Test
 *
 * @author Archit Shah (ashah@arsdigita.com)
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */

public class SessionManagerTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/SessionManagerTest.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public SessionManagerTest(String name) {
        super(name);
    }

    public void setUp() {
        SessionManager.setSchemaConnectionInfo(
            "Test",
            "jdbc:oracle:thin:@dev0103-001.arsdigita.com:1522:ora8i",
            "planitia",
            "planitiarules"
            );
    }

    public void tearDown() {
        SessionManager.resetSchemaConnectionInfo();
    }

    public void testSessionManager() {
        Session s = SessionManager.getSession();
        assertNotNull(s);
    }

    public void testSessionPushPop() {
        Session s = SessionManager.getSession();
        // make sure that the stack trace does not return anything
        assert("The stack string should be blank", s.getStackTrace() == "" ||
               s.getStackTrace() == null);

        String var1 = "This is my first message";
        String var2 = "This is my second message";

        s.pushMessage(var1);
        s.pushMessage(var2);
        String trace = s.getStackTrace();
        assert("The stack trace should contain both var1 and var2",
               trace.indexOf(var1) > -1 && trace.indexOf(var2) > -1);

        assert("var 2 should show up before var1 in the trace",
               trace.indexOf(var1) > trace.indexOf(var2));

    }
}
