package com.arsdigita.persistence.proto.engine.rdbms;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * RDBMSQuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/26 $
 **/

public class RDBMSQuerySource extends QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/engine/rdbms/RDBMSQuerySource.java#1 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    private DynamicQuerySource m_dqs = new DynamicQuerySource();
    private StaticQuerySource m_sqs = new StaticQuerySource();

    private boolean isStatic(ObjectMap om) {
        return om.getRetrieves().size() != 0 ||
            om.getRetrieveAll() != null;
    }

    private boolean isStatic(ObjectMap om, Property prop) {
        Path path = Path.get(prop.getName());
        Mapping m = om.getMapping(path);
        return m == null || m.getRetrieve() != null;
    }

    public Query getQuery(ObjectType type, Object key) {
        ObjectMap om = Root.getRoot().getObjectMap(type);
        if (isStatic(om)) {
            return m_sqs.getQuery(type, key);
        } else {
            return m_dqs.getQuery(type, key);
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
        if (isStatic(om)) {
            return m_sqs.getQuery(obj, prop);
        } else {
            return m_dqs.getQuery(obj, prop);
        }
    }

}
