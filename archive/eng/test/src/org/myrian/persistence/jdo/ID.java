/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.jdo;

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
