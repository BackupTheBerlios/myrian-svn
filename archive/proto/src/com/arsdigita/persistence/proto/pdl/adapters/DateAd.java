package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * DateAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/18 $
 **/

public class DateAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/DateAd.java#1 $ by $Author: rhs $, $DateTime: 2003/03/18 15:44:06 $";

    public DateAd() {
	super(Root.getRoot().getObjectType("global.Date"));
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	Timestamp tstamp = new Timestamp(((java.util.Date) obj).getTime());
	ps.setTimestamp(index, tstamp);
    }
    public Object fetch(ResultSet rs, String column) throws SQLException {
	Timestamp tstamp = rs.getTimestamp(column);
	if (tstamp == null) {
	    return null;
	} else {
	    return new java.util.Date(tstamp.getTime());
	}
    }

}
