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

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

/**
 * RDBMSQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/27 $
 **/

public class RDBMSQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/engine/rdbms/RDBMSQuerySource.java#3 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

    private DynamicQuerySource m_dqs = new DynamicQuerySource();
    private StaticQuerySource m_sqs = new StaticQuerySource();

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
        ObjectMap om = type.getRoot().getObjectMap(type);
        if (isStatic(om)) {
            return m_sqs.getQuery(type);
        } else {
            return m_dqs.getQuery(type);
        }
    }

    public Query getQuery(PropertyMap keys) {
        ObjectType ot = keys.getObjectType();
        ObjectMap om = ot.getRoot().getObjectMap(ot);
        if (isStatic(om)) {
            return m_sqs.getQuery(keys);
        } else {
            return m_dqs.getQuery(keys);
        }
    }

    public Query getQuery(Object obj) {
        ObjectMap om = getSession().getObjectMap(obj);
        if (isStatic(om)) {
            return m_sqs.getQuery(obj);
        } else {
            return m_dqs.getQuery(obj);
        }
    }

    public Query getQuery(Object obj, Property prop) {
        ObjectMap om = getSession().getObjectMap(obj);
        if (isStatic(om, prop)) {
            return m_sqs.getQuery(obj, prop);
        } else {
            return m_dqs.getQuery(obj, prop);
        }
    }

}
