/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.redhat.persistence.common;

/**
 * CompoundKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public final class CompoundKey {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/common/CompoundKey.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Object m_one;
    private Object m_two;

    public CompoundKey(Object one, Object two) {
        m_one = one;
        m_two = two;
    }

    public int hashCode() {
        return (m_one == null ? 0 : m_one.hashCode()) ^
            (m_two == null ? 0 : m_two.hashCode());
    }

    private static final boolean compare(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof CompoundKey) {
            CompoundKey key = (CompoundKey) o;
            return compare(m_one, key.m_one) && compare(m_two, key.m_two);
        } else {
            return false;
        }
    }

    public String toString() {
        return "key(" + m_one + ", " + m_two + ")";
    }

}
