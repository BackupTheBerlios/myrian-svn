package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.*;
import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

/**
 * RDBMSQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class RDBMSQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/engine/rdbms/RDBMSQuerySource.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

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
        ObjectMap om = Root.getRoot().getObjectMap(type);
        if (isStatic(om)) {
            return m_sqs.getQuery(type);
        } else {
            return m_dqs.getQuery(type);
        }
    }

    public Query getQuery(PropertyMap keys) {
        ObjectMap om = Root.getRoot().getObjectMap(keys.getObjectType());
        if (isStatic(om)) {
            return m_sqs.getQuery(keys);
        } else {
            return m_dqs.getQuery(keys);
        }
    }

    public Query getQuery(Object obj) {
        ObjectMap om = Session.getObjectMap(obj);
        if (isStatic(om)) {
            return m_sqs.getQuery(obj);
        } else {
            return m_dqs.getQuery(obj);
        }
    }

    public Query getQuery(Object obj, Property prop) {
        ObjectMap om = Session.getObjectMap(obj);
        if (isStatic(om, prop)) {
            return m_sqs.getQuery(obj, prop);
        } else {
            return m_dqs.getQuery(obj, prop);
        }
    }

}
