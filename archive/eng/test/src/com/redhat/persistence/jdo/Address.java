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

/**
 * Address
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class Address {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Address.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private String m_street;
    private String m_apt;
    private String m_city;
    private String m_state;
    private String m_zip;

    public Address() {}

    public void setStreet(String str) {
        m_street = str;
    }

    public String getStreet() {
        return m_street;
    }

    public void setApt(String str) {
        m_apt = str;
    }

    public String getApt() {
        return m_apt;
    }

    public void setCity(String str) {
        m_city = str;
    }

    public String getCity() {
        return m_city;
    }

    public void setState(String str) {
        m_state = str;
    }

    public String getState() {
        return m_state;
    }

    public void setZip(String str) {
        m_zip = str;
    }

    public String getZip() {
        return m_zip;
    }

}
