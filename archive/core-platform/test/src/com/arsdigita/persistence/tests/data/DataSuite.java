package com.arsdigita.persistence.tests.data;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * DataSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/10/31 $
 **/

public class DataSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/tests/data/DataSuite.java#1 $ by $Author: rhs $, $DateTime: 2002/10/31 12:25:15 $";

    public DataSuite() {}

    public DataSuite(Class theClass) {
        super(theClass);
    }

    public DataSuite(String name) {
        super(name);
    }

    public static Test suite() {
        DataSuite suite = new DataSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.persistence.Initializer");
        wrapper.setSetupSQLScript(System.getProperty("test.sql.dir") +
                                  "/persistence/setup.sql");
        wrapper.setTeardownSQLScript(System.getProperty("test.sql.dir") +
                                     "/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
