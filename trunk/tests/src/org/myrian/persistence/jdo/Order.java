/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**
 * Order
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
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
