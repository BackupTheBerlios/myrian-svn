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
 * CharacterAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/04/05 $
 **/

public class CharacterAd extends SimpleAdapter {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/pdl/adapters/CharacterAd.java#2 $ by $Author: rhs $, $DateTime: 2004/04/05 15:33:44 $";

    public CharacterAd() {
	super("global.Character", Types.CHAR);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	Character charObj = (Character)obj;
	if (charObj == null ||
	    "".equals(charObj.toString())) {
	    ps.setString(index, null);
	} else {
	    ps.setString(index, charObj.toString());
	}
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	String str = rs.getString(column);
	if (str != null && str.length() > 0) {
	    return new Character(str.charAt(0));
	} else {
	    return null;
	}
    }

}
