package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.metadata.*;


/**
 * SimpleAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/18 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/SimpleAdapter.java#1 $ by $Author: rhs $, $DateTime: 2003/03/18 15:44:06 $";

    private ObjectType m_type;

    protected SimpleAdapter(ObjectType type) {
	if (type == null) { throw new IllegalArgumentException(); }
	m_type = type;
    }

    public PropertyMap getProperties(Object obj) {
	return new PropertyMap(m_type);
    }

    public ObjectType getObjectType(Object obj) { return m_type; }

}
