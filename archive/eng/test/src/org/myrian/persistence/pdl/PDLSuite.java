package org.myrian.persistence.pdl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * PDLSuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class PDLSuite extends TestSuite {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/org/myrian/persistence/pdl/PDLSuite.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    public static Test suite() {
        PDLSuite suite = new PDLSuite();
        suite.addTestSuite(ErrorTest.class);
        suite.addTestSuite(ParameterTest.class);
        return suite;
    }

}
