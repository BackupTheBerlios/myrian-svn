package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * LongAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class LongAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/LongAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public LongAd() {
	super(Root.getRoot().getObjectType("global.Long"), Types.BIGINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setLong(index, ((Long) obj).longValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	long l = rs.getLong(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Long(l);
	}
    }

}
