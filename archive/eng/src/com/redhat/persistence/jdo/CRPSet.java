package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;

import java.util.*;

/**
 * CRPSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/06/22 $
 **/

class CRPSet extends CRPCollection implements Set {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/jdo/CRPSet.java#1 $ by $Author: vadim $, $DateTime: 2004/06/22 13:25:03 $";

    private Object m_object;
    private Property m_property;

    CRPSet(Session ssn, Object object, Property property) {
        super(ssn);
        m_object = object;
        m_property = property;
    }

    ObjectType type() {
        return m_property.getType();
    }

    Expression expression() {
        return new Get(new Literal(m_object), m_property.getName());
    }

    public void clear() {
        m_ssn.clear(m_object, m_property);
    }

    public boolean add(Object o) {
        if (contains(o)) {
            return false;
        } else {
            Main.lock(m_ssn, m_object);
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
            Main.lock(m_ssn, m_object);
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

}
