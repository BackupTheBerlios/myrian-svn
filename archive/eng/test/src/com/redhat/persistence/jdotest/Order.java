package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Order
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Order {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Order.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract int getId();

    public abstract Collection getItems();

    public abstract Party getParty();

    public abstract void setParty(Party party);

    public abstract Date getPurchaseDate();

    public String toString() {
        return "<order #" + getId() + ">";
    }

}
