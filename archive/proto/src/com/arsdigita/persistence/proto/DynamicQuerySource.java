package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * DynamicQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/04/10 $
 **/

public class DynamicQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DynamicQuerySource.java#4 $ by $Author: ashah $, $DateTime: 2003/04/10 17:19:22 $";

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
        Filter f = null;

        for (Iterator it = keyProps.iterator(); it.hasNext(); ) {
            Property keyProp = (Property) it.next();
            Object key = keys.get(keyProp);
            Parameter keyParam = new Parameter
                (keyProp.getType(), Path.add("__key__", keyProp.getName()));
            sig.addParameter(keyParam);
            query.set(keyParam, key);
            Filter propFilt = new EqualsFilter
                (Path.get(keyProp.getName()), keyParam.getPath());

            if (f == null) {
                f = propFilt;
            } else {
                f = new AndFilter(f, propFilt);
            }
        }

        return new Query(query, f);
    }

    public Query getQuery(Object obj) {
        ObjectType type = Session.getObjectType(obj);
        Signature sig = getSignature(type);
        Parameter start = new Parameter(type, Path.get("__start__"));
        sig.addParameter(start);
        Query q = new Query
            (sig, new EqualsFilter(Path.get("__start__"), null));
        q.set(start, obj);
        return q;
    }

    public Query getQuery(Object obj, Property prop) {
        if (prop.getType().isKeyed()) {
            ObjectType type = prop.getType();
            Signature sig = getSignature(type);
            Parameter start = new Parameter(prop.getContainer(),
                                            Path.get("__start__"));
            sig.addParameter(start);

	    Filter f;
	    if (prop.isCollection()) {
		f = new ContainsFilter
		    (Path.add(start.getPath(), prop.getName()), null);
	    } else {
		f = new EqualsFilter
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
            Parameter start = new Parameter(type, Path.get("__start__"));
            sig.addParameter(start);
            Query q = new Query
                (sig, new EqualsFilter(start.getPath(), null));
            q.set(start, obj);
            return q;
        }
    }

}
