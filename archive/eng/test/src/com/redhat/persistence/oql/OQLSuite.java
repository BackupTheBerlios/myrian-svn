package com.redhat.persistence.oql;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * OQLSuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

public class OQLSuite extends TestSuite {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/oql/OQLSuite.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 11:52:43 $";

    public static Test suite() {
        OQLSuite suite = new OQLSuite();

        suite.addTestSuite(CodeTest.class);
        suite.addTest(QuerySuite.suite());

        return suite;
    }

}
