package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;
import java.sql.*;

/**
 * Adapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #9 $ $Date: 2003/04/18 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Adapter.java#9 $ by $Author: rhs $, $DateTime: 2003/04/18 15:09:07 $";

    private static final Map ADAPTERS = new HashMap();

    public static final void addAdapter(Class javaClass, Adapter ad) {
        ADAPTERS.put(javaClass, ad);
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
	    Class klass = ot.getJavaClass();
	    if (klass != null) {
		Adapter a = getAdapter(klass);
		if (a != null) { return a; }
	    }
        }

        Adapter a = (Adapter) ADAPTERS.get(null);
        if (a != null) { return a; }

        throw new IllegalArgumentException("no adapter for: " + type);
    }

    Object getSessionKey(Object obj) {
        return getSessionKey(getObjectType(obj), getProperties(obj));
    }

    Object getSessionKey(ObjectType type, PropertyMap props) {
        Collection keys = type.getKeyProperties();
        Object key = null;

        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Object value = props.get((Property) it.next());
            if (key == null) {
                key = value;
            } else {
                key = new CompoundKey(key, value);
            }
        }

        return new CompoundKey(type.getBasetype(), key);
    }

    // This needs work. It's odd to have an adapter interface here in the
    // session layer that knows about prepared statements. Also I don't like
    // having things that throw unsupported operation exception.

    public Object fetch(ResultSet rs, String column) throws SQLException {
        throw new UnsupportedOperationException("not a bindable type");
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
        throws SQLException {
        throw new UnsupportedOperationException("not a bindable type");
    }

    public void setSession(Object obj, Session ssn) { return; }

    public Object getObject(ObjectType basetype, PropertyMap props) {
        throw new UnsupportedOperationException("not a compound type");
    }

    public abstract PropertyMap getProperties(Object obj);

    public abstract ObjectType getObjectType(Object obj);

}
