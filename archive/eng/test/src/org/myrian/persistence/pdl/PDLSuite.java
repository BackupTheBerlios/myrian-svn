package org.myrian.persistence.pdl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * PDLSuite
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class PDLSuite extends TestSuite {


    public static Test suite() {
        PDLSuite suite = new PDLSuite();
        suite.addTestSuite(ErrorTest.class);
        suite.addTestSuite(ParameterTest.class);
        return suite;
    }

}
