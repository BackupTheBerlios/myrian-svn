package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/02/12 $
 **/

public abstract class RecordSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/RecordSet.java#6 $ by $Author: ashah $, $DateTime: 2003/02/12 16:39:50 $";

    private Signature m_signature;
    private Adapter m_adapter;

    protected RecordSet(Signature signature) {
        m_signature = signature;
        m_adapter = Adapter.getAdapter(m_signature.getObjectType());
    }

    public Signature getSignature() {
        return m_signature;
    }

    public abstract boolean next();

    public abstract Object get(Path p);

    Object load(Session ssn) {
        Collection paths = m_signature.getPaths();
        ObjectType type = m_signature.getObjectType();

        HashMap keys = new HashMap();

        // First load up the objects.
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path parent = p.getParent();
            if (type.isKey(p)) {
                Object value = get(p);
                Map key;
                if (keys.containsKey(parent)) {
                    key = (Map) keys.get(parent);
                } else {
                    if (value == null) {
                        key = null;
                    } else {
                        key = new HashMap();
                    }
                    keys.put(parent, key);
                }

                if (value != null) {
                    key.put(p.getName(), value);
                }
            }
        }

        HashMap objs = new HashMap();

        // Instantiate all the objects
        for (Iterator it = keys.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            if (me.getValue() == null) {
                objs.put(me.getKey(), null);
            } else {
                Path p = (Path) me.getKey();
                if (p == null) {
                    objs.put(p, m_adapter.load
                             (type.getBasetype(), (Map) me.getValue()));
                } else {
                    objs.put(p, m_adapter.load
                             (m_signature.getProperty(p).getType(),
                              (Map) me.getValue()));
                }
            }
        }

        // Now load up the values.
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path parent = p.getParent();

            Object obj = objs.get(parent);

            if (obj == null) {
                Object value = get(p);
                if (value != null) {
                    throw new IllegalStateException
                        ("key was null but value isn't");
                }
            } else {
                Property prop = m_signature.getProperty(p);
                ssn.load(obj, prop, get(p));
            }
        }

        return objs.get(null);
    }

}
