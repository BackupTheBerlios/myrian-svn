package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;


/**
 * LongAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class LongAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/LongAd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
