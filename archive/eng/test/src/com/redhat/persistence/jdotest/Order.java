package com.redhat.persistence.jdotest;

import java.util.Collection;
import java.util.Date;

/**
 * Order
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/23 $
 **/
public class Order {
    private int id;
    private Collection items;
    private Party party;
    private Date purchaseDate;

    public int getId() {
        return id;
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
