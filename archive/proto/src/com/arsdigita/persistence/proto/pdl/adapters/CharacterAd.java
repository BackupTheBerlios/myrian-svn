package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * CharacterAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class CharacterAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/CharacterAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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
