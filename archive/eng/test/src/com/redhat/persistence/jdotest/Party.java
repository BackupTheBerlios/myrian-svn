package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Party
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Party {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Party.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract String getEmail();

    public abstract void setEmail(String email);

    public abstract List getAuxiliaryEmails();

    public void setAuxiliaryEmails(List emails) {
        List l = getAuxiliaryEmails();
        l.clear();
        l.addAll(emails);
    }

}
