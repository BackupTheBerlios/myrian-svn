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
 * DoubleAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class DoubleAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/pdl/adapters/DoubleAd.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public DoubleAd() {
	super("global.Double", Types.DOUBLE);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setDouble(index, ((Double) obj).doubleValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	double d = rs.getDouble(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Double(d);
	}
    }

}
