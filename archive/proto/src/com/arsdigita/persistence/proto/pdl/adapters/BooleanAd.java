package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * BooleanAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/04/10 $
 **/

public class BooleanAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BooleanAd.java#2 $ by $Author: ashah $, $DateTime: 2003/04/10 17:19:22 $";

    public BooleanAd() {
	super(Root.getRoot().getObjectType("global.Boolean"));
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        if (Types.CHAR == type) {
            if (Boolean.TRUE.equals(obj)) {
                ps.setString(index, "1");
            } else {
                ps.setString(index, "0");
            }
        } else {
            ps.setBoolean(index, ((Boolean) obj).booleanValue());
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
