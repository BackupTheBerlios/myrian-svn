/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;
import com.redhat.persistence.DynamicQuerySource;
import com.redhat.persistence.Expression;
import com.redhat.persistence.NoSuchPathException;
import com.redhat.persistence.Parameter;
import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.Query;
import com.redhat.persistence.Signature;
import com.redhat.persistence.Source;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Mapping;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.metadata.Static;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * RDBMSQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class RDBMSQuerySource extends DynamicQuerySource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/engine/rdbms/RDBMSQuerySource.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private synchronized Source getSource(ObjectType type, SQLBlock block,
                                          Path prefix) {
        Source src = QGen.getSource(block);

        if (src == null) {
            src = new Source(type);
            QGen.addSource(src, block, prefix);
        } else {
            src = QGen.getSource(block);
        }

        return src;
    }

    private Signature getSignature(ObjectType type, SQLBlock block,
                                   Path prefix, ObjectType from) {
	return getSignature(type, block, prefix, from, true);
    }

    private Signature getSignature(ObjectType type, SQLBlock block,
                                   Path prefix, ObjectType from,
				   boolean requireKey) {
        Signature sig = new Signature(getSource(type, block, prefix));

        for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
	    try {
		sig.addPath(Path.relative(prefix, path));
	    } catch (NoSuchPathException e) {
		throw new MetadataException
		    (type.getRoot(), block,
                     "mapping not in signature: " + e.getPath());
	    }
        }

	ArrayList unfetched = null;

        for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
            Path fetched = (Path) it.next();
            Path parent = Path.relative(prefix, fetched.getParent());
            if ((!requireKey || !type.isKeyed()) && parent == null) {
                continue;
            }
            ObjectType ot = sig.getType(parent);
            Path[] paths = RDBMSEngine.getKeyPaths
                (ot, Path.add(prefix, parent));
            for (int i = 0; i < paths.length; i++) {
                if (!block.hasMapping(paths[i])) {
                    if (unfetched == null) {
                        unfetched = new ArrayList();
                    }
                    unfetched.add(paths[i]);
                }
            }
        }

	if (unfetched != null) {
	    throw new MetadataException
		(type.getRoot(), block,
                 "unfetched immediate properties: " + unfetched);
	}

	if (from != null) {
            for (Iterator it = from.getKeyProperties().iterator();
                 it.hasNext(); ) {
                Property key = (Property) it.next();
                Parameter p = new Parameter
                    (key.getType(), Path.get(":" + key.getName()));
                sig.addParameter(p);
		Path kp = Path.get(key.getName());
		if (block.getMapping(Path.add(prefix, kp)) == null) {
		    p = new Parameter(key.getType(), kp);
		    sig.addParameter(p);
		}
            }
        }

        return sig;
    }

    private boolean isStatic(ObjectMap om) {
        return om.getRetrieves().size() != 0 ||
            om.getRetrieveAll() != null;
    }

    private boolean isStatic(ObjectMap om, Property prop) {
        Path path = Path.get(prop.getName());
        Mapping m = om.getMapping(path);
        return m instanceof Static || m.getRetrieve() != null;
    }

    public Query getQuery(ObjectType type) {
        ObjectMap om = getSession().getRoot().getObjectMap(type);
        if (!isStatic(om)) {
            return super.getQuery(type);
        }

        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        return new Query(sig, null);
    }

    public Query getQuery(PropertyMap keys) {
        ObjectType type = keys.getObjectType();
        ObjectMap om = getSession().getRoot().getObjectMap(type);

        if (!isStatic(om)) {
            return super.getQuery(keys);
        }

        Collection keyProps = om.getKeyProperties();

        if (om.getRetrieveAll() == null) {
            return getQuery(om, keys, (Property) keyProps.iterator().next());
        }

        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        Query query = new Query(sig, null);
        Expression f = null;

        for (Iterator it = keyProps.iterator(); it.hasNext(); ) {
            Property keyProp = (Property) it.next();
            Object key = keys.get(keyProp);
            Parameter keyParam = new Parameter
                (keyProp.getType(), Path.add("key__", keyProp.getName()));
            sig.addParameter(keyParam);
            query.set(keyParam, key);
            Expression propFilt = Condition.equals
                (Path.get(keyProp.getName()), keyParam.getPath());

            if (f == null) {
                f = propFilt;
            } else {
                f = Condition.and(f, propFilt);
            }
        }

        return new Query(query, f);
    }

    public Query getQuery(Object obj) {
        ObjectMap om = getSession().getObjectMap(obj);
        if (!isStatic(om)) {
            return super.getQuery(obj);
        }

        ObjectType type = om.getObjectType();
        if (om.getRetrieveAll() == null) {
            Property key =
                (Property) type.getKeyProperties().iterator().next();
            return getQuery(obj, key);
        }
        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        Parameter start = new Parameter(type, Path.get("start__"));
        sig.addParameter(start);
        Query result = new Query
            (sig, Condition.equals(null, start.getPath()));
        result.set(start, obj);
        return result;
    }

    public Query getQuery(Object obj, Property prop) {
        ObjectMap om = getSession().getObjectMap(obj);
        if (!isStatic(om, prop)) {
            return super.getQuery(obj, prop);
        }

        return getQuery(getSession().getObjectMap(obj),
                        getSession().getProperties(obj),
                        prop);
    }

    private Query getQuery(ObjectMap om, PropertyMap props, Property prop) {
        Path path = Path.get(prop.getName());
        Signature sig;
        SQLBlock block;
        if (prop.isCollection()) {
            ObjectType type = prop.getType();
            Mapping m = om.getMapping(path);
            block = m.getRetrieve();
            if (block == null) {
                throw new MetadataException(prop.getRoot(), prop,
                                            "no retrieve for " + prop);
            }
            sig = getSignature(type, block, path, props.getObjectType());
        } else {
            ObjectType type = om.getObjectType();
            sig = null;
            for (Iterator it = om.getRetrieves().iterator(); it.hasNext(); ) {
                block = (SQLBlock) it.next();
                if (block.hasMapping(path) ||
                    om.getKeyProperties().contains(prop)) {
                    sig = getSignature
                        (type, block, null, props.getObjectType(), false);
                    if (!sig.hasPath(path)) {
                        sig.addPath(path);
                    }
                    break;
                }
            }

            if (sig == null) {
                Mapping m = om.getMapping(path);
                block = m.getRetrieve();
                if (block == null) {
                    throw new MetadataException
                        (prop.getRoot(), prop, "no retrieve for " + prop);
                }
                sig = getSignature
                    (prop.getType(), block, path, props.getObjectType());
            }
        }

        Query result = new Query(sig, null);

        for (Iterator it = props.getObjectType().getKeyProperties().iterator();
             it.hasNext(); ) {
            Property key = (Property) it.next();
            Object value = props.get(key);
            Parameter p = sig.getParameter(Path.get(":" + key.getName()));
            result.set(p, value);
	    p = sig.getParameter(Path.get(key.getName()));
	    if (p != null) {
		result.set(p, value);
	    }
        }

        return result;
    }

}
