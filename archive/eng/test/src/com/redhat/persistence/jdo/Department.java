package com.redhat.persistence.jdo;

import java.math.BigInteger;

public class Department {
    private String name = null;

    public Department() { }

    public Department(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
