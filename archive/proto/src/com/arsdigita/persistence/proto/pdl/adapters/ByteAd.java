package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * ByteAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class ByteAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/ByteAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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
