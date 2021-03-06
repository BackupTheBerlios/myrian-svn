package com.redhat.persistence.common;

/**
 * CompoundKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public final class CompoundKey {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/common/CompoundKey.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
