/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;
import org.apache.log4j.Logger;

/**
 * PersistenceWrapper
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */
public class PersistenceWrapper extends BaseTestSetup {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/PersistenceWrapper.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
