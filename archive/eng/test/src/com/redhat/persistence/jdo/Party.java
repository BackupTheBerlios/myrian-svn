/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.jdo;

import java.util.*;

import org.apache.log4j.Logger;
/**
 * Party
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
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
