package com.redhat.persistence.jdo;

import java.util.*;

/**
 * Rolodex
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

public class Rolodex {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Rolodex.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 08:43:09 $";

    private Set m_contacts = new HashSet();

    public Rolodex() {}

    public Collection getContacts() {
        return m_contacts;
    }

}
