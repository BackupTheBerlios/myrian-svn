package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * DoubleAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class DoubleAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/adapters/DoubleAd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public DoubleAd() {
	super(Root.getRoot().getObjectType("global.Double"), Types.DOUBLE);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setDouble(index, ((Double) obj).doubleValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	double d = rs.getDouble(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Double(d);
	}
    }

}
