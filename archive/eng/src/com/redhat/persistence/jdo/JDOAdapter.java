package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.spi.*;

import org.apache.log4j.Logger;

public class JDOAdapter extends Adapter {
    private static final Logger s_log = Logger.getLogger(JDOAdapter.class);

    public Object getObject(ObjectType base, PropertyMap props, Session ssn) {
        if (ssn == null) { throw new NullPointerException("ssn"); }

        ObjectType type = props.getObjectType();
        Class klass = type.getJavaClass();
        StateManagerImpl smi = new StateManagerImpl(getPMI(ssn));
        PersistenceCapable pc = JDOImplHelper.getInstance().newInstance
            (klass, smi);
        smi.cacheKeyProperties(pc, props);

        return pc;
    }

    public PropertyMap getProperties(Object obj) {
        // XXX: do we need this? (copied from c.a.p)
        // if (obj instanceof PropertyMap) { return (PropertyMap) obj; }

        PersistenceCapable pc = (PersistenceCapable) obj;
        ObjectType type = getObjectType(obj);
        return C.pmap(pc, type);
    }

    public ObjectType getObjectType(Object obj) {
        PersistenceCapable pc = (PersistenceCapable) obj;
        return C.type((PersistenceCapable) obj);
    }

    private static PersistenceManagerImpl getPMI(Session ssn) {
        PersistenceManagerImpl pmi = null;
        synchronized(ssn) {
            pmi = (PersistenceManagerImpl) ssn.getAttribute(pmi.ATTR_NAME);
            if (pmi == null) {
                pmi = new PersistenceManagerImpl(ssn);
                ssn.setAttribute(pmi.ATTR_NAME, pmi);
            } else {
                s_log.debug("getPMI: hit");
            }
            return pmi;
        }
    }
}
