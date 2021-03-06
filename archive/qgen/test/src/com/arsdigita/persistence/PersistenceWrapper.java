/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import junit.framework.*;
import org.apache.log4j.Logger;

/**
 * PersistenceWrapper
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/01/29 $
 */
public class PersistenceWrapper extends CoreTestSetup {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/persistence/PersistenceWrapper.java#2 $ by $Author: ashah $, $DateTime: 2004/01/29 12:35:08 $";

    private static Logger s_log =
        Logger.getLogger(PersistenceWrapper.class.getName());

    public PersistenceWrapper(TestSuite suite) {
        super(suite);
        setSetupSQLScript("/com/arsdigita/persistence/oracle-se/setup.sql");
        setTeardownSQLScript("/com/arsdigita/persistence/oracle-se/teardown.sql");

    }

    public static Test suite(){
        final String className = System.getProperty("junit.test");
        s_log.info("Class name: " + className);
        TestSuite suite = new TestSuite();
        if( null != className ) {
            try {
                Class testClass = Class.forName(className);
                suite.addTestSuite( testClass );
            }
            catch(ClassNotFoundException e) {

            }


        }
        return new PersistenceWrapper(suite);

    }
}
