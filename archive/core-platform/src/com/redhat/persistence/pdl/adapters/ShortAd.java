package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * ShortAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class ShortAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/ShortAd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public ShortAd() {
	super(Root.getRoot().getObjectType("global.Short"), Types.SMALLINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setShort(index, ((Short) obj).shortValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	short s = rs.getShort(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Short(s);
	}
    }

}
