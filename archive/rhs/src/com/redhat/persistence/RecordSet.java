/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public abstract class RecordSet {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/RecordSet.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private static final Logger LOG = Logger.getLogger(RecordSet.class);

    private Signature m_signature;

    protected RecordSet(Signature signature) {
        m_signature = signature;
    }

    protected Signature getSignature() {
        return m_signature;
    }

    boolean isFetched(Path path) {
        return m_signature.isFetched(path);
    }

    public abstract boolean next();

    public abstract Object get(Path p);

    public abstract void close();

    Map load(Session ssn) {
        Collection paths = m_signature.getPaths();

	LinkedList remaining = new LinkedList();
	for (Iterator it = paths.iterator(); it.hasNext(); ) {
	    Path path = (Path) it.next();

            for (; ; path = path.getParent()) {
                if (!remaining.contains(path)) {
                    remaining.add(path);
                }
                if (m_signature.isSource(path)) {
                    break;
                }
            }
        }

	HashMap values = new HashMap();
	int before;
	do {
	    before = remaining.size();

	    OUTER: for (Iterator it = remaining.iterator(); it.hasNext(); ) {
		Path p = (Path) it.next();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("loading " + p);
                }
		ObjectType type = m_signature.getType(p);
                Adapter adapter = ssn.getRoot().getAdapter(type);
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
                                (adapter.getSessionKey(type, pmap));

                            if (previous != null) {
                                ObjectType prevType =
                                    adapter.getObjectType(previous);

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
			    obj = adapter.getObject(type, pmap);
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

        HashMap cursorValues = new HashMap();

	for (Iterator it = values.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry me = (Map.Entry) it.next();
	    Path p = (Path) me.getKey();
            Object value = me.getValue();

	    if (m_signature.isSource(p)) {
                cursorValues.put(p, value);
                continue;
            }

	    Property prop = m_signature.getProperty(p);
            if (prop.getContainer().isKeyed() && !prop.isCollection()) {
		Object container = values.get(p.getParent());
		if (container == null) {
		    if (value == null) {
			continue;
		    } else {
			throw new IllegalStateException
                            ("container of " + p + " is null");
		    }
		}
		ssn.load(container, prop, value);
	    } else {
                cursorValues.put(p, value);
            }
	}

	return cursorValues;
    }

}
