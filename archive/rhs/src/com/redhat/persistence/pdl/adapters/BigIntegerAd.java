/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.adapters;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * BigIntegerAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class BigIntegerAd extends SimpleAdapter {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/adapters/BigIntegerAd.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    public BigIntegerAd() {
	super("global.BigInteger", Types.NUMERIC);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setBigDecimal(index, new BigDecimal((BigInteger) obj));
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	BigDecimal bd = rs.getBigDecimal(column);
	if (bd == null) {
	    return null;
	} else {
	    return bd.toBigInteger();
	}
    }

}
