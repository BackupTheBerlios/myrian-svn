package com.redhat.persistence.jdotest;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Date;

/**
 * Order
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/06/24 $
 **/
public class Order {
    private int id;
    private Collection items;
    private Party party;
    private Date purchaseDate;

    public Order() {
        items = new LinkedList();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collection getItems() {
        return items;
    }

    public void setItems(Collection items) {
        this.items = items;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public String toString() {
        return "<order #" + getId() + ">";
    }

}
