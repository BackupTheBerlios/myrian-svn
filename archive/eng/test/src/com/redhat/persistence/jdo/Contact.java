package com.redhat.persistence.jdo;

/**
 * Contact
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/18 $
 **/

public class Contact {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Contact.java#2 $ by $Author: rhs $, $DateTime: 2004/08/18 14:57:34 $";

    private String m_name;
    private String m_number;

    private Contact() {}

    public Contact(String name, String number) {
        m_name = name;
        m_number = number;
    }

    public Contact(String name) {
        this(name, null);
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
