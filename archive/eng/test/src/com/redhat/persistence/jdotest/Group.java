package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Group
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Group extends Party {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Group.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract String getName();

    public abstract void setName(String name);

    public abstract Collection getUsers();

    public String toString() {
        return getName();
    }

}
