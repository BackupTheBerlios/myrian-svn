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

package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;


/**
 * SimpleAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/

abstract class SimpleAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/SimpleAdapter.java#2 $ by $Author: rhs $, $DateTime: 2003/08/19 22:28:24 $";

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
