package com.redhat.persistence.jdotest;

import java.math.BigInteger;

public class Department {
    private BigInteger id = null;
    private String name = null;

    public Department() { }

    public Department(BigInteger id, String name) {
        this.id = id;
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (o instanceof Department) {
            Department d = (Department) o;
            if (id == null) {
                return (d.id == null);
            } else {
                return id.equals(d.id);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        return getId() == null ? 0 : id.hashCode();
    }

    public String toString() {
        return "dep " + getId();
    }
}
