/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.common;

/**
 * CompoundKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public final class CompoundKey {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/common/CompoundKey.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

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
