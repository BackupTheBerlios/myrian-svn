package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * StaticQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

class StaticQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/engine/rdbms/StaticQuerySource.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
		    (block, "mapping not in signature: " + e.getPath());
	    }
        }

	ArrayList unfetched = null;
	for (Iterator it = sig.getObjectType()
		 .getImmediateProperties().iterator(); it.hasNext(); ) {
	    Property prop = (Property) it.next();
	    Path p = Path.get(prop.getName());
	    if (!requireKey && sig.getObjectType().isKey(p)) {
		continue;
	    }
	    Path[] paths = RDBMSEngine.getKeyPaths
		(prop.getType(), Path.add(prefix, p));
	    for (int i = 0; i < paths.length; i++) {
		if (block.getMapping(paths[i]) == null) {
		    if (unfetched == null) {
			unfetched = new ArrayList();
		    }
		    unfetched.add(p);
		}
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
		Path kp = Path.get(key.getName());
		if (block.getMapping(Path.add(prefix, kp)) == null) {
		    p = new Parameter(key.getType(), kp);
		    sig.addParameter(p);
		}
            }
        }

        return sig;
    }

    public Query getQuery(ObjectType type) {
        ObjectMap om = Root.getRoot().getObjectMap(type);
        Signature sig = getSignature(type, om.getRetrieveAll(), null, null);
        return new Query(sig, null);
    }

    public Query getQuery(PropertyMap keys) {
        ObjectType type = keys.getObjectType();
        ObjectMap om = Root.getRoot().getObjectMap(type);
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
        ObjectMap om = Session.getObjectMap(obj);
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
                sig = getSignature
                    (prop.getType(), block, path, props.getObjectType());
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
	    if (p != null) {
		result.set(p, value);
	    }
        }

        return result;
    }

}
