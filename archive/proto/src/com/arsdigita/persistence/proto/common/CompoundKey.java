package com.arsdigita.persistence.proto.common;

/**
 * CompoundKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/12 $
 **/

public final class CompoundKey {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/common/CompoundKey.java#1 $ by $Author: rhs $, $DateTime: 2003/02/12 14:25:00 $";

    private Object m_one;
    private Object m_two;

    public CompoundKey(Object one, Object two) {
        m_one = one;
        m_two = two;
    }

    public int hashCode() {
        return m_one.hashCode() ^ m_two.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof CompoundKey) {
            CompoundKey key = (CompoundKey) o;
            return m_one.equals(key.m_one) && m_two.equals(key.m_two);
        } else {
            return false;
        }
    }

}
