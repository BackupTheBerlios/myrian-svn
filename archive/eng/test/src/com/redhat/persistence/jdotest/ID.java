package com.redhat.persistence.jdotest;

import java.io.Serializable;
import java.math.BigInteger;

public class ID implements Serializable {
    public BigInteger id;

    public ID() { }

    public boolean equals(Object o) {
        if (o == null) { return false; }

        if (o instanceof ID) {
            if (!getClass().equals(o.getClass())) {
                return false;
            }

            if (id == null) {
                return ((ID) o).id == null;
            }

            return id.equals(((ID) o).id);

        }
        return false;
    }

    public int hashCode() {
        if (id == null) { return 0; }
        return id.hashCode();
    }
}
