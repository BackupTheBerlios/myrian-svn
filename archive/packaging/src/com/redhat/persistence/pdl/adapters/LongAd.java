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
 * LongAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/27 $
 **/

public class LongAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/LongAd.java#3 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

    public LongAd() {
	super("global.Long", Types.BIGINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setLong(index, ((Long) obj).longValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	long l = rs.getLong(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Long(l);
	}
    }

}
