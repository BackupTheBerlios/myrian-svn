package com.redhat.persistence.oql;

import junit.framework.*;

/**
 * CodeTest
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

public class CodeTest extends TestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/oql/CodeTest.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 11:52:43 $";

    public void testMultipleAdds() {
        Code a = new Code("a");
        Code ab = a.add("b");
        Code ac = a.add("c");
        assertEquals("a", a.getSQL());
        assertEquals("ab", ab.getSQL());
        assertEquals("ac", ac.getSQL());
    }

}
