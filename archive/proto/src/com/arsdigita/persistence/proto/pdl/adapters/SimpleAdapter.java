package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;


/**
 * SimpleAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/05/08 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/SimpleAdapter.java#3 $ by $Author: rhs $, $DateTime: 2003/05/08 15:05:52 $";

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
