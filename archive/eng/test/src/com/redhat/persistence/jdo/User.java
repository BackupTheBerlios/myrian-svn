package com.redhat.persistence.jdo;

/**
 * User
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/29 $
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
}
