package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;


/**
 * SimpleAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/SimpleAdapter.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private ObjectType m_type;
    private int m_defaultJDBCType;

    protected SimpleAdapter(ObjectType type, int defaultJDBCType) {
	if (type == null) { throw new IllegalArgumentException(); }
	m_type = type;
        m_defaultJDBCType = defaultJDBCType;
    }

    public PropertyMap getProperties(Object obj) {
	return new PropertyMap(m_type);
    }

    public ObjectType getObjectType(Object obj) { return m_type; }

    public int defaultJDBCType() { return m_defaultJDBCType; }

    public boolean isMutation(Object value, int jdbcType) {
        return false;
    }

}
