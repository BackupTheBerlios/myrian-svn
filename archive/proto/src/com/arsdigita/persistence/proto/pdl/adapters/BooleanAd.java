package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * BooleanAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/05/07 $
 **/

public class BooleanAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BooleanAd.java#4 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

    public BooleanAd() {
	super(Root.getRoot().getObjectType("global.Boolean"), Types.BIT);
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
