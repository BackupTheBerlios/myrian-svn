package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

import org.apache.log4j.Logger;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #10 $ $Date: 2003/02/17 $
 **/

public abstract class RecordSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/RecordSet.java#10 $ by $Author: rhs $, $DateTime: 2003/02/17 20:13:29 $";

    private static final Logger LOG = Logger.getLogger(RecordSet.class);

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

        HashMap pmaps = new HashMap();

        // First load up the objects.
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path parent = p.getParent();
            if (type.isKey(p)) {
                Object value = get(p);
                PropertyMap props;
                if (pmaps.containsKey(parent)) {
                    props = (PropertyMap) pmaps.get(parent);
                } else {
                    if (value == null) {
                        props = null;
                    } else {
                        props = new PropertyMap();
                    }
                    pmaps.put(parent, props);
                }

                if (value != null) {
                    props.put(type.getProperty(p), value);
                }
            }
        }

        HashMap objs = new HashMap();

        // Instantiate all the objects
        for (Iterator it = pmaps.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Path p = (Path) me.getKey();
            PropertyMap props = (PropertyMap) me.getValue();
            if (props == null) {
                objs.put(p, null);
            } else {
                ObjectType ot;
                if (p == null) {
                    ot = type;
                } else {
                    ot = m_signature.getProperty(p).getType();
                }
                Object obj = ssn.getObject(m_adapter.getSessionKey(ot, props));
                if (obj == null) {
                    obj = m_adapter.getObject(ot, props);
                    m_adapter.setSession(obj, ssn);
                }
                objs.put(p, obj);
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
                if (objs.containsKey(p)) {
                    ssn.load(obj, prop, objs.get(p));
                } else {
                    ssn.load(obj, prop, get(p));
                }
            }
        }

        return objs.get(null);
    }

}
