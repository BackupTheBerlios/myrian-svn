package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

import java.util.*;

/**
 * DynamicQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/02/26 $
 **/

public class DynamicQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/DynamicQuerySource.java#2 $ by $Author: rhs $, $DateTime: 2003/02/26 20:44:08 $";

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

    public Query getQuery(ObjectType type, Object key) {
        Signature sig = getSignature(type);
        ObjectMap map = Root.getRoot().getObjectMap(type);
        Collection keys = map.getKeyProperties();
        if (keys.size() != 1) {
            throw new IllegalArgumentException("type has more than one key");
        }
        Property keyProp = (Property) keys.iterator().next();
        Parameter keyParam = new Parameter
            (keyProp.getType(), Path.get("__key__"));
        sig.addParameter(keyParam);
        Filter f =
            new EqualsFilter(Path.get(keyProp.getName()), keyParam.getPath());
        Query result = new Query(sig, f);
        result.set(keyParam, key);
        return result;
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
        if (prop.isCollection()) {
            ObjectType type = prop.getType();
            Signature sig = getSignature(type);
            Parameter start = new Parameter(prop.getContainer(),
                                            Path.get("__start__"));
            sig.addParameter(start);

            // should filter to associated object(s)
            // should deal with one way associations
            Filter f = new ContainsFilter
                (Path.get("__start__." + prop.getName()), null);
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
                (sig, new EqualsFilter(Path.get("__start__"), null));
            q.set(start, obj);
            return q;
        }
    }

}
