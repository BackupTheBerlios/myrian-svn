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

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * IntegerAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/15 $
 **/

public class IntegerAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/IntegerAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    public IntegerAd() {
	super(Root.getRoot().getObjectType("global.Integer"), Types.INTEGER);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setInt(index, ((Integer) obj).intValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	int i = rs.getInt(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Integer(i);
	}
    }

}
