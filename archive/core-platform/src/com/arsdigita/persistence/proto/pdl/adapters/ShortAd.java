package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * ShortAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class ShortAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/adapters/ShortAd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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