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

import java.util.*;

import org.apache.log4j.Logger;
/**
 * Party
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class Party {
    private final static Logger s_log = Logger.getLogger(Party.class);

    private int id;
    private String email;
    private List auxiliaryEmails;

    public Party() {}

    public Party(int id) {
        this.id = id;
        auxiliaryEmails = new LinkedList();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List getAuxiliaryEmails() {
        return auxiliaryEmails;
    }

    public void setAuxiliaryEmails(List emails) {
        s_log.debug("auxiliaryEmails.class=" + auxiliaryEmails.getClass());
        auxiliaryEmails.clear();
        auxiliaryEmails.addAll(emails);
    }

    public String toString() {
        return "<party #" + id + ">";
    }
}
