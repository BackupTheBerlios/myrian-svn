package com.redhat.persistence.jdo;

/**
 * Address
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/05 $
 **/

public class Address {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Address.java#1 $ by $Author: rhs $, $DateTime: 2004/08/05 12:06:26 $";

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
