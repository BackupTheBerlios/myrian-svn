package com.arsdigita.persistence.oql;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import junit.framework.Test;

/**
 * OQLSuite
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 **/

public class OQLSuite extends PackageTestSuite {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/OQLSuite.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

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
        wrapper.setInitScriptTarget("com.arsdigita.persistence.Initializer");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(suite());
    }

}
