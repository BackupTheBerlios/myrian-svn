package com.redhat.persistence;

import java.util.*;

/**
 * Violation
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

interface Violation {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/Violation.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 08:43:09 $";

    Collection getDependentEvents();

    String getViolationMessage();

}
