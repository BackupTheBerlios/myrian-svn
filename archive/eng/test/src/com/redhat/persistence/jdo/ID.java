/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.jdo;

import java.io.Serializable;
import java.math.BigInteger;

public final class ID implements Serializable {
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
