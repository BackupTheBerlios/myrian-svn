package com.redhat.persistence.jdotest;

import java.util.*;

import org.apache.log4j.Logger;
/**
 * Party
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/06/28 $
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
