package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * DynamicQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class DynamicQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/DynamicQuerySource.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private Signature getSignature(ObjectType type) {
        Signature result = new Signature(type);
        result.addDefaultProperties();
        return result;
    }

    public Query getQuery(ObjectType type) {
        Signature sig = new Signature(type);
        sig.addDefaultProperties();
        return new Query(sig, null);
    }

    public Query getQuery(PropertyMap keys) {
        ObjectType type = keys.getObjectType();
        Signature sig = getSignature(type);
        ObjectMap map = Root.getRoot().getObjectMap(type);
        Collection keyProps = map.getKeyProperties();

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
        ObjectType type = Session.getObjectType(obj);
        Signature sig = getSignature(type);
        Parameter start = new Parameter(type, Path.get("start__"));
        sig.addParameter(start);
        Query q = new Query
            (sig, Condition.equals(start.getPath(), null));
        q.set(start, obj);
        return q;
    }

    public Query getQuery(Object obj, Property prop) {
        if (prop.getType().isKeyed()) {
            ObjectType type = prop.getType();
            Signature sig = getSignature(type);
            Parameter start = new Parameter(prop.getContainer(),
                                            Path.get("start__"));
            sig.addParameter(start);

	    Expression f;
	    if (prop.isCollection()) {
		f = Condition.contains
		    (Path.add(start.getPath(), prop.getName()), null);
	    } else {
		f = Condition.equals
		    (Path.add(start.getPath(), prop.getName()), null);
	    }
            Query q = new Query(sig, f);
            q.set(start, obj);
            return q;
        } else {
            ObjectType type = Session.getObjectType(obj);
            Signature sig = new Signature(type);
            sig.addPath(prop.getName());
            sig.addDefaultProperties(Path.get(prop.getName()));
            Parameter start = new Parameter(type, Path.get("start__"));
            sig.addParameter(start);
            Query q = new Query
                (sig, Condition.equals(start.getPath(), null));
            q.set(start, obj);
            return q;
        }
    }

}
