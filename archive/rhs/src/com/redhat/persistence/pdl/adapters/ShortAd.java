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
 * ShortAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/11/09 $
 **/

public class ShortAd extends SimpleAdapter {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/adapters/ShortAd.java#1 $ by $Author: rhs $, $DateTime: 2003/11/09 14:41:17 $";

    public ShortAd() {
	super("global.Short", Types.SMALLINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setShort(index, ((Short) obj).shortValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	short s = rs.getShort(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Short(s);
	}
    }

}
