package com.redhat.persistence.common;

/**
 * IdentityKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/08/05 $
 **/

public class IdentityKey {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/common/IdentityKey.java#1 $ by $Author: rhs $, $DateTime: 2004/08/05 12:04:47 $";

    private Object m_object;

    public IdentityKey(Object object) {
        m_object = object;
    }

    public int hashCode() {
        return System.identityHashCode(m_object);
    }

    public boolean equals(Object other) {
        if (other instanceof IdentityKey) {
            IdentityKey key = (IdentityKey) other;
            return m_object == key.m_object;
        } else {
            return false;
        }
    }

}
