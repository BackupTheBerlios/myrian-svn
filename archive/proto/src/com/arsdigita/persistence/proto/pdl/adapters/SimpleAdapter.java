package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;


/**
 * SimpleAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/SimpleAdapter.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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

}
