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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * ObjectAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/10/28 $
 **/

public class ObjectAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/ObjectAd.java#4 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    public ObjectAd() {
	super("global.Object", Types.INTEGER);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setObject(index, obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	return rs.getObject(column);
    }

}
