package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Adapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/14 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Adapter.java#6 $ by $Author: ashah $, $DateTime: 2003/02/14 01:21:43 $";

    private static final Map ADAPTERS = new HashMap();

    public static final void addAdapter(Class javaClass, ObjectType type,
                                        Adapter ad) {
        ADAPTERS.put(javaClass, ad);
        ADAPTERS.put(type, ad);
    }

    public static final Adapter getAdapter(Class javaClass) {
        for (Class c = javaClass; c != null; c = c.getSuperclass()) {
            Adapter a = (Adapter) ADAPTERS.get(c);
            if (a != null) { return a; }
        }

        throw new IllegalArgumentException("no adapter for: " + javaClass);
    }

    public static final Adapter getAdapter(ObjectType type) {

        for (ObjectType ot = type; ot != null; ot = ot.getSupertype()) {
            Adapter a = (Adapter) ADAPTERS.get(ot);
            if (a != null) { return a; }
        }

        Adapter a = (Adapter) ADAPTERS.get(null);
        if (a != null) { return a; }

        throw new IllegalArgumentException("no adapter for: " + type);
    }

    Object getSessionKey(Object obj) {
        return getSessionKey(getObjectType(obj), getProperties(obj));
    }

    Object getSessionKey(ObjectType basetype, PropertyMap props) {
        Collection keys = basetype.getKeyProperties();
        Object key = null;

        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Object value = props.get((Property) it.next());
            if (key == null) {
                key = value;
            } else {
                key = new CompoundKey(key, value);
            }
        }

        return new CompoundKey(basetype, key);
    }

    public void setSession(Object obj, Session ssn) { return; }

    public Object getObject(ObjectType basetype, PropertyMap props) {
        throw new UnsupportedOperationException("not a compound type");
    }

    public abstract PropertyMap getProperties(Object obj);

    public abstract ObjectType getObjectType(Object obj);

}
