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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.persistence.*;
import junit.framework.*;

/**
 * DataSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2004/09/01 $
 **/

public class DataSuite extends TestSuite {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/tests/data/DataSuite.java#3 $ by $Author: rhs $, $DateTime: 2004/09/01 10:15:50 $";

    public DataSuite() {}

    public DataSuite(Class theClass) {
        super(theClass);
    }

    public DataSuite(String name) {
        super(name);
    }

    public static Test suite() {
        DataSuite suite = new DataSuite();
        suite.addTestSuite(CRUDTest.class);
        suite.addTestSuite(MappingsTest.class);
        PersistenceTestSetup wrapper = new PersistenceTestSetup(suite);
        wrapper.addSQLSetupScript("com/arsdigita/persistence/setup.sql");
        wrapper.addSQLTeardownScript("com/arsdigita/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
