package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Adapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/12 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Adapter.java#2 $ by $Author: ashah $, $DateTime: 2003/02/12 16:39:50 $";

    private static final Map ADAPTERS = new HashMap();

    public static final void addAdapter(Class javaClass, ObjectType type,
                                        Adapter ad) {
        ADAPTERS.put(javaClass, ad);
        ADAPTERS.put(type, ad);
    }

    public static final Adapter getAdapter(Class javaClass) {
        for (; javaClass != null; javaClass = javaClass.getSuperclass()) {
            Adapter a = (Adapter) ADAPTERS.get(javaClass);
            if (a != null) { return a; }
        }

        throw new IllegalArgumentException("no adapter for: " + javaClass);
    }

    public static final Adapter getAdapter(ObjectType type) {
        for (; type != null; type = type.getSupertype()) {
            Adapter a = (Adapter) ADAPTERS.get(type);
            if (a != null) { return a; }
        }

        throw new IllegalArgumentException("no adapter for: " + type);
    }

    Object getSessionKey(Object obj) {
        return new CompoundKey(getObjectType(obj), getKey(obj));
    }

    public Object getJDBC(Object java, int type) {
        throw new UnsupportedOperationException("not a simple type");
    }

    public Object getJava(Object jdbc, int type) {
        throw new UnsupportedOperationException("not a simple type");
    }

    public Object load(ObjectType baseType, Map properties) {
        throw new UnsupportedOperationException("not a compound type");
    }

    public Object get(Object obj, Property prop) {
        throw new UnsupportedOperationException("not a compound type");
    }

    public Object getKey(Object obj) {
        throw new UnsupportedOperationException("not a keyed type");
    }

    public ObjectType getObjectType(Object obj) {
        throw new UnsupportedOperationException("not a keyed type");
    }

}
