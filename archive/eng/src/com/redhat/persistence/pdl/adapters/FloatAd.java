/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.pdl.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * FloatAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/09/07 $
 **/

public class FloatAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/FloatAd.java#3 $ by $Author: dennis $, $DateTime: 2004/09/07 10:26:15 $";

    public FloatAd() {
	super("global.Float", Types.FLOAT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setFloat(index, ((Float) obj).floatValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	float f = rs.getFloat(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Float(f);
	}
    }

}
