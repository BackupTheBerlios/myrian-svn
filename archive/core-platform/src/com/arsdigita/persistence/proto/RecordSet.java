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
 * @version $Revision: #2 $ $Date: 2003/06/02 $
 **/

public abstract class RecordSet {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/RecordSet.java#2 $ by $Author: rhs $, $DateTime: 2003/06/02 10:49:07 $";

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

    public abstract void close();

    Map load(Session ssn) {
        Collection paths = m_signature.getPaths();

	LinkedList remaining = new LinkedList();
	for (Iterator it = paths.iterator(); it.hasNext(); ) {
	    Path path = (Path) it.next();
	    for (Path p = path; p != null; p = p.getParent()) {
		if (!remaining.contains(p)) {
		    remaining.add(p);
		}
	    }
	}

	if (!remaining.contains(null)) {
	    remaining.add(null);
	}

	HashMap values = new HashMap();
	int before;
	do {
	    before = remaining.size();

	    OUTER: for (Iterator it = remaining.iterator(); it.hasNext(); ) {
		Path p = (Path) it.next();
		ObjectType type = m_signature.getType(p);
		Collection props = type.getImmediateProperties();
		if (props.size() == 0) {
		    values.put(p, get(p));
		    it.remove();
		} else {
		    PropertyMap pmap = new PropertyMap(type);
		    for (Iterator iter = props.iterator(); iter.hasNext(); ) {
			Property prop = (Property) iter.next();
			Path kp = Path.add(p, prop.getName());
			if (values.containsKey(kp)) {
			    pmap.put(prop, values.get(kp));
			} else if (m_signature.isFetched(kp)) {
			    continue OUTER;
			}
		    }

		    Object obj = null;
		    if (!pmap.isNull()) {
                        Object previous = null;
			if (type.isKeyed()) {
                            previous = ssn.getObject
                                (m_adapter.getSessionKey(type, pmap));

                            if (previous != null) {
                                ObjectType prevType =
                                    m_adapter.getObjectType(previous);

                                if (type.equals(prevType)
                                    || prevType.isSubtypeOf(type)) {
                                    obj = previous;
                                } else if (!type.isSubtypeOf(prevType)) {
                                    throw new IllegalStateException
                                        ("object of wrong type in session "
                                         + type + " " + prevType);
                                }
                            }
			}

			if (obj == null) {
			    obj = m_adapter.getObject(type, pmap);
			    if (type.isKeyed()) {
				m_adapter.setSession(obj, ssn);
			    }

                            if (previous != obj) {
                                ssn.use(obj);
                            }
			}
		    }
		    values.put(p, obj);
		    it.remove();
		}
	    }
	} while (remaining.size() < before);

	if (remaining.size() > 0) {
	    throw new IllegalStateException
		("unable to load the following paths: " + remaining +
		 "\nsignature: " + m_signature);
	}

	for (Iterator it = values.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    Path p = (Path) me.getKey();
	    if (m_signature.isSource(p)) { continue; }
	    Property prop = m_signature.getProperty(p);
	    if (prop.getContainer().isKeyed()) {
		Object container = values.get(p.getParent());
		Object value = me.getValue();
		if (container == null) {
		    if (value == null) {
			continue;
		    } else {
			throw new IllegalStateException
			    ("container is null but value isn't");
		    }
		}
		ssn.load(container, prop, value);
	    }
	}

	return values;
    }

}
