package com.redhat.persistence.jdo;

/**
 * Item
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/29 $
 **/

public class Item {

    private int id;
    private Product product;

    public Item() {}

    public Item(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String toString() {
        return "<item #" + getId() + ">";
    }

}
