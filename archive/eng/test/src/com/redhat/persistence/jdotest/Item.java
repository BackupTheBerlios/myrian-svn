package com.redhat.persistence.jdotest;

/**
 * Item
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Item {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Item.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract int getId();

    public abstract Product getProduct();

    public abstract void setProduct(Product product);

    public String toString() {
        return "<item #" + getId() + ">";
    }

}
