package com.arsdigita.persistence.proto;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * ProtoSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2003/01/06 $
 **/

public class ProtoSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoSuite.java#2 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    public ProtoSuite() {}

    public ProtoSuite(Class theClass) {
        super(theClass);
    }

    public ProtoSuite(String name) {
        super(name);
    }

    public static Test suite() {
        ProtoSuite suite = new ProtoSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.logging.Initializer");
//        wrapper.setSetupSQLScript(System.getProperty("test.sql.dir") +
//                                  "/persistence/setup.sql");
//        wrapper.setTeardownSQLScript(System.getProperty("test.sql.dir") +
//                                     "/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
