package com.redhat.persistence.jdotest;

/**
 * User
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/06/25 $
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
