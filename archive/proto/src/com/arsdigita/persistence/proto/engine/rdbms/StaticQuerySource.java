package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * StaticQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/04/04 $
 **/

class StaticQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/StaticQuerySource.java#6 $ by $Author: rhs $, $DateTime: 2003/04/04 18:09:54 $";

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
        Signature sig = new Signature(getSource(type, block, prefix));

        for (Iterator it = block.getPaths().iterator(); it.hasNext(); ) {
            Path path = (Path) it.next();
	    Path toAdd;
	    if (prefix == null) {
		toAdd = path;
	    } else {
		toAdd = prefix.getRelative(path);
	    }
	    try {
		sig.addPath(toAdd);
	    } catch (NoSuchPathException e) {
		throw new MetadataException(block, "mapping not in signature");
	    }
        }

	ArrayList unfetched = null;
	for (Iterator it = sig.getObjectType()
		 .getImmediateProperties().iterator(); it.hasNext(); ) {
	    Property prop = (Property) it.next();
	    Path p = Path.get(prop.getName());
	    if (!sig.isFetched(p)) {
		if (unfetched == null) {
		    unfetched = new ArrayList();
		}
		unfetched.add(p);
	    }
	}

	if (unfetched != null) {
	    throw new MetadataException
		(block, "unfetched immediate properties: " + unfetched);
	}

        if (from != null) {
            for (Iterator it = from.getKeyProperties().iterator();
                 it.hasNext(); ) {
                Property key = (Property) it.next();
                Parameter p = new Parameter
                    (key.getType(), Path.get(":" + key.getName()));
                sig.addParameter(p);
		p = new Parameter
		    (key.getType(), Path.get(key.getName()));
		sig.addParameter(p);
            }
        }

        return sig;
    }

    public Query getQuery(ObjectType type) {
        ObjectMap om = Root.getRoot().getObjectMap(type);
        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        return new Query(sig, null);
    }

    public Query getQuery(ObjectType type, Object key) {
        ObjectMap om = Root.getRoot().getObjectMap(type);
        Collection keys = om.getKeyProperties();
        if (keys.size() != 1) {
            throw new IllegalArgumentException("type has more than one key");
        }
        Property keyProp = (Property) keys.iterator().next();

        if (om.getRetrieveAll() == null) {
            PropertyMap props = new PropertyMap(type);
            props.put(keyProp, key);
            return getQuery(om, props, keyProp);
        }

        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        Parameter keyParam = new Parameter
            (keyProp.getType(), Path.get("__key__"));
        sig.addParameter(keyParam);
        Filter f = new EqualsFilter
            (Path.get(keyProp.getName()), keyParam.getPath());
        Query result = new Query(sig, f);
        result.set(keyParam, key);
        return result;
    }

    public Query getQuery(Object obj) {
        ObjectMap om = Session.getObjectMap(obj);
        ObjectType type = om.getObjectType();
        if (om.getRetrieveAll() == null) {
            Property key =
                (Property) type.getKeyProperties().iterator().next();
            return getQuery(obj, key);
        }
        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        Parameter start = new Parameter(type, Path.get("__start__"));
        sig.addParameter(start);
        Query result = new Query
            (sig, new EqualsFilter(null, start.getPath()));
        result.set(start, obj);
        return result;
    }

    public Query getQuery(Object obj, Property prop) {
        return getQuery(Session.getObjectMap(obj), Session.getProperties(obj),
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
            sig = getSignature(type, block, path, props.getObjectType());
        } else {
            ObjectType type = om.getObjectType();
            sig = null;
            for (Iterator it = om.getRetrieves().iterator(); it.hasNext(); ) {
                block = (SQLBlock) it.next();
                if (block.hasMapping(path) ||
                    om.getKeyProperties().contains(prop)) {
                    sig = getSignature
                        (type, block, null, props.getObjectType());
                    if (!sig.hasPath(path)) {
                        sig.addPath(path);
                    }
                    break;
                }
            }

            if (sig == null) {
                Mapping m = om.getMapping(path);
                block = m.getRetrieve();
                sig = getSignature
                    (prop.getType(), block, null, props.getObjectType());
            }
        }

        Query result = new Query(sig, null);

        for (Iterator it = props.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry me = (Map.Entry) it.next();
            Property key = (Property) me.getKey();
            Object value = me.getValue();
            Parameter p = sig.getParameter(Path.get(":" + key.getName()));
            result.set(p, value);
	    p = sig.getParameter(Path.get(key.getName()));
	    result.set(p, value);
        }

        return result;
    }

}
