package com.arsdigita.persistence.proto;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * ProtoSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/05/12 $
 **/

public class ProtoSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/proto/ProtoSuite.java#4 $ by $Author: rhs $, $DateTime: 2003/05/12 15:39:36 $";

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
        wrapper.setInitScriptTarget("com.arsdigita.db.Initializer");
        wrapper.setSetupSQLScript
            (System.getProperty("test.sql.dir") +
             "/com/arsdigita/persistence/setup.sql");
        wrapper.setTeardownSQLScript
            (System.getProperty("test.sql.dir") +
             "/com/arsdigita/persistence/teardown.sql");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
