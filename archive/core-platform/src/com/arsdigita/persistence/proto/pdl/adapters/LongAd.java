package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * LongAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class LongAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/adapters/LongAd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
