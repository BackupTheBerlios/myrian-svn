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

/**
 * Address
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class Address {


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
