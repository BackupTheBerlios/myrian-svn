package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Party
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/06/25 $
 **/

public class Party {
    private int id;
    private String email;

    public Party() {}

    public Party(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List getAuxiliaryEmails() {
        return new LinkedList();
    }

    public void setAuxiliaryEmails(List emails) {
        List l = getAuxiliaryEmails();
        l.clear();
        l.addAll(emails);
    }

    public String toString() {
        return "<party #" + id + ">";
    }
}
