/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
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
 * BooleanAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public class BooleanAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/BooleanAd.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public BooleanAd() {
	super("global.Boolean", Types.BIT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        // Because postgres has a boolean type and we are currently
        // using char(1) and the jdbc driver uses "false" and "true"
        // we have to do this converstion.  Long term, we want
        // to remove this
        if (Boolean.TRUE.equals(obj)) {
            ps.setString(index, "1");
        } else {
            ps.setString(index, "0");
        }
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	boolean bool = rs.getBoolean(column);
	if (rs.wasNull()) {
	    return null;
	} else if (bool) {
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }
}
