package com.redhat.persistence.jdo;

/**
 * Contact
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/06 $
 **/

public class Contact {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Contact.java#1 $ by $Author: rhs $, $DateTime: 2004/08/06 08:43:09 $";

    private String m_name;
    private String m_number;

    private Contact() {}

    public Contact(String name) {
        m_name = name;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getNumber() {
        return m_number;
    }

    public void setNumber(String number) {
        m_number = number;
    }

}
