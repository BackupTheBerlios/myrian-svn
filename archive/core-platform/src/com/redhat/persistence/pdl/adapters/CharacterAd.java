package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;

/**
 * CharacterAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class CharacterAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/CharacterAd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public CharacterAd() {
	super(Root.getRoot().getObjectType("global.Character"), Types.CHAR);
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