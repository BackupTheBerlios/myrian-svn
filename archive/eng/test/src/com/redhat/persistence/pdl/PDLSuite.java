package com.redhat.persistence.pdl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * PDLSuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/09/14 $
 **/

public class PDLSuite extends TestSuite {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/pdl/PDLSuite.java#1 $ by $Author: rhs $, $DateTime: 2004/09/14 17:22:30 $";

    public static Test suite() {
        PDLSuite suite = new PDLSuite();
        suite.addTestSuite(ErrorTest.class);
        return suite;
    }

}
