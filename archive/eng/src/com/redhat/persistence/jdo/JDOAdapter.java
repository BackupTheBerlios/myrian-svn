package com.redhat.persistence.jdo;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import java.util.*;
import javax.jdo.spi.*;

public class JDOAdapter extends Adapter {

    public Object getObject(ObjectType base, PropertyMap props, Session ssn) {
        if (ssn == null) { throw new NullPointerException("ssn"); }

        ObjectType type = props.getObjectType();
        Class klass = type.getJavaClass();
        StateManagerImpl smi =
            new StateManagerImpl(new PersistenceManagerImpl(ssn));
        PersistenceCapable pc = JDOImplHelper.getInstance().newInstance
            (klass, smi);
        smi.cacheKeyProperties(pc, props);

        return pc;
    }

    public void setSession(Object obj, Session ssn) {
        // XXX: better to not construct new PMI every time
        ((PersistenceCapable) obj).jdoReplaceStateManager
            (new StateManagerImpl(
                new PersistenceManagerImpl(ssn)));
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
}
