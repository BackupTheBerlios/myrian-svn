package com.arsdigita.persistence.oql;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * OQLSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class OQLSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/OQLSuite.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public OQLSuite() {}

    public OQLSuite(Class theClass) {
        super(theClass);
    }

    public OQLSuite(String name) {
        super(name);
    }

    public static Test suite() {
        OQLSuite suite = new OQLSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        wrapper.setInitScriptTarget("com.arsdigita.logging.Initializer");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
