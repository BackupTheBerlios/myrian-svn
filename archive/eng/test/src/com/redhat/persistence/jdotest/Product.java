package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Product
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

public abstract class Product {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdotest/Product.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    public abstract int getId();

    public abstract String getName();

    public abstract void setName(String name);

    public abstract float getPrice();

    public abstract void setPrice(float price);

    public abstract Picture getPicture();

    public abstract void setPicture(Picture picture);

    public String toString() {
        return "<product #" + getId() + ">";
    }

}
