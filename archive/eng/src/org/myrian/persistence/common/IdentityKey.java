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
package org.myrian.persistence.common;

/**
 * IdentityKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/10/01 $
 **/

public class IdentityKey {

    public final static String versionId = "$Id: //eng/persistence/dev/src/org/myrian/persistence/common/IdentityKey.java#1 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

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
