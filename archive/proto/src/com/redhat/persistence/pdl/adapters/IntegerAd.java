package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * IntegerAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class IntegerAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/IntegerAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public IntegerAd() {
	super(Root.getRoot().getObjectType("global.Integer"), Types.INTEGER);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setInt(index, ((Integer) obj).intValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	int i = rs.getInt(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Integer(i);
	}
    }

}
