package com.redhat.persistence.jdotest;

/**
 * User
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/24 $
 **/

public class User extends Party {

    private String name;

    public User() {}

    public User(int id) {
        super(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        String name = getName();
        if (name == null) {
            return getEmail();
        } else {
            return name;
        }
    }

}
