package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.sql.*;

/**
 * ByteAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class ByteAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/ByteAd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    public ByteAd() {
	super(Root.getRoot().getObjectType("global.Byte"), Types.TINYINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setByte(index, ((Byte) obj).byteValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	byte b = rs.getByte(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Byte(b);
	}
    }

}
