package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.oql.*;
import com.redhat.persistence.oql.Expression;
import java.util.*;
import javax.jdo.JDOHelper;
import javax.jdo.spi.PersistenceCapable;

import org.apache.log4j.Logger;

/**
 * CRPSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/07/14 $
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
        if (s_log.isDebugEnabled()) {
            s_log.debug("CRPSet.add: " + o);
        }

        PersistenceManagerImpl pmi =
            (PersistenceManagerImpl) JDOHelper.getPersistenceManager(m_object);

        if (C.isComponentProperty(m_property)) {
            pmi.makePersistent((PersistenceCapable) o, m_property.getType());
        } else {
            pmi.makePersistent(o);
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

}
