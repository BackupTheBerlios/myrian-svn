package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * Adapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/12 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Adapter.java#1 $ by $Author: rhs $, $DateTime: 2003/02/12 14:21:42 $";

    private static final Map ADAPTERS = new HashMap();

    public static final void addAdapter(Class javaClass, Adapter ad) {
        ADAPTERS.put(javaClass, ad);
    }

    public static final Adapter getAdapter(Class javaClass) {
        return (Adapter) ADAPTERS.get(javaClass);
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

    public Object load(Map properties) {
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
