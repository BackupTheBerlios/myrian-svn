package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * FloatAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class FloatAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/FloatAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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
