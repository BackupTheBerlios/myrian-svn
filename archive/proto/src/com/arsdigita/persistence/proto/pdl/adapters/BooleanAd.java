package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * BooleanAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/04/15 $
 **/

public class BooleanAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BooleanAd.java#3 $ by $Author: ashah $, $DateTime: 2003/04/15 10:07:23 $";

    public BooleanAd() {
	super(Root.getRoot().getObjectType("global.Boolean"));
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        if (Types.BIT == type) {
            ps.setBoolean(index, ((Boolean) obj).booleanValue());
        } else {
            if (Boolean.TRUE.equals(obj)) {
                ps.setString(index, "1");
            } else {
                ps.setString(index, "0");
            }
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
