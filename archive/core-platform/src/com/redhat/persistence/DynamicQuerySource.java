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

package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

import java.util.*;

/**
 * DynamicQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/10/23 $
 **/

public class DynamicQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/DynamicQuerySource.java#3 $ by $Author: justin $, $DateTime: 2003/10/23 15:28:18 $";

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
        ObjectMap map = type.getRoot().getObjectMap(type);
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
        ObjectType type = getSession().getObjectType(obj);
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
            ObjectType type = getSession().getObjectType(obj);
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
