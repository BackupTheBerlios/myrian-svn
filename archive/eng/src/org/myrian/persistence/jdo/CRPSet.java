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

import org.myrian.persistence.*;
import org.myrian.persistence.metadata.*;
import org.myrian.persistence.oql.*;
import org.myrian.persistence.oql.Expression;
import java.util.*;
import javax.jdo.JDOHelper;
import javax.jdo.spi.PersistenceCapable;

import org.apache.log4j.Logger;

/**
 * CRPSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

class CRPSet extends CRPCollection implements Set {

    private final static Logger s_log = Logger.getLogger(CRPSet.class);

    private Session m_ssn;
    private Object m_object;
    private Property m_property;

    CRPSet(Session ssn, Object object, Property property) {
        m_ssn = ssn;
        m_object = object;
        m_property = property;
    }

    Session ssn() {
        return m_ssn;
    }

    ObjectType type() {
        return m_property.getType();
    }

    public Expression expression() {
        return new Get(new Literal(m_object), m_property.getName());
    }

    public void clear() {
        m_ssn.clear(m_object, m_property);
    }

    public boolean add(Object o) {
        PersistenceManagerImpl pmi =
            (PersistenceManagerImpl) JDOHelper.getPersistenceManager(m_object);

        if (C.isComponentProperty(m_property)) {
            pmi.makePersistent(m_object, m_property, (PersistenceCapable) o);
        } else {
            pmi.makePersistent(m_object, m_property, o);
        }

        if (contains(o)) {
            return false;
        } else {
            C.lock(m_ssn, m_object);
            if (contains(o)) {
                return false;
            } else {
                m_ssn.add(m_object, m_property, o);
                return true;
            }
        }
    }

    public boolean remove(Object o) {
        if (contains(o)) {
            C.lock(m_ssn, m_object);
            if (contains(o)) {
                m_ssn.remove(m_object, m_property, o);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Set)) { return false; }

        Set set = (Set) obj;

        if (size() != set.size()) { return false; }

        for (Iterator it=iterator(); it.hasNext(); ) {
            if (!set.contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = 0;
        for (Iterator it=iterator(); it.hasNext(); ) {
            Object elem = it.next();
            result += elem == null ? 0 : elem.hashCode();
        }
        return result;
    }
}
