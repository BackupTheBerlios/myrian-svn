package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * FloatAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class FloatAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/FloatAd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public FloatAd() {
	super(Root.getRoot().getObjectType("global.Float"), Types.FLOAT);
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
