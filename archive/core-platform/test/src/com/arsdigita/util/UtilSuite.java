/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Priority;
import org.apache.log4j.SimpleLayout;

/**
 * @author Dennis Gregorovic
 * @version $Revision: #8 $ $Date: 2004/04/07 $
 */
public class UtilSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/UtilSuite.java#8 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public static Test suite() {
        initializeLogging();
        UtilSuite suite = new UtilSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
//        wrapper.setPerformInitialization(false);
        return wrapper;
    }

    private static void initializeLogging() {
        ConsoleAppender log = new ConsoleAppender(new SimpleLayout());
        log.setThreshold (Priority.toPriority("warn"));
        BasicConfigurator.configure(log);
    }

}
