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

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.SimpleLayout;

/**
 * @author Dennis Gregorovic
 * @version $Revision: #4 $ $Date: 2003/08/04 $
 */
public class UtilSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/util/UtilSuite.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public static Test suite() {
        initializeLogging();
        UtilSuite suite = new UtilSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setPerformInitialization(false);
        return wrapper;
    }

    private static void initializeLogging() {
        ConsoleAppender log = new ConsoleAppender(new SimpleLayout());
        log.setThreshold (Priority.toPriority("warn"));
        BasicConfigurator.configure(log);
    }

}
