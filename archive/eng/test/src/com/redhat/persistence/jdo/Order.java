package com.redhat.persistence.jdo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Order
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/12 $
 **/
public class Order {
    private int id;
    private Set items;
    private Party party;
    private Date purchaseDate;

    public Order() {
        items = new HashSet();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set getItems() {
        return items;
    }

    public void setItems(Set items) {
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
