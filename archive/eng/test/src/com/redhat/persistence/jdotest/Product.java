package com.redhat.persistence.jdotest;

/**
 * Product
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/28 $
 **/

public class Product {

    private int id;
    private String name;
    private float price;
    private Picture picture;

    public Product() {}

    public Product(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.price = price;
    }

    public String toString() {
        return "<product #" + getId() + ">";
    }

}
