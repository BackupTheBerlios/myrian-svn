package com.redhat.persistence.jdotest;

import java.util.*;

/**
 * Group
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/06/25 $
 **/
public class Group extends Party {
    private String name;
    private Collection users;

    public Group() {}

    public Group(int id) {
        super(id);
        users = new LinkedList();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection getUsers() {
        return users;
    }

    public String toString() {
        return getName();
    }
}
