package com.redhat.persistence.jdo;

/**
 * Item
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/29 $
 **/

public abstract class Item {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Item.java#1 $ by $Author: vadim $, $DateTime: 2004/06/29 15:38:35 $";

    public abstract int getId();

    public abstract Product getProduct();

    public abstract void setProduct(Product product);

    public String toString() {
        return "<item #" + getId() + ">";
    }

}
