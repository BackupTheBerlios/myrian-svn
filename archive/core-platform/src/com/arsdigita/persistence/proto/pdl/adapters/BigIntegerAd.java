package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.math.*;
import java.sql.*;

/**
 * BigIntegerAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class BigIntegerAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/adapters/BigIntegerAd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public BigIntegerAd() {
	super(Root.getRoot().getObjectType("global.BigInteger"),
              Types.NUMERIC);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setBigDecimal(index, new BigDecimal((BigInteger) obj));
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	BigDecimal bd = rs.getBigDecimal(column);
	if (bd == null) {
	    return null;
	} else {
	    return bd.toBigInteger();
	}
    }

}
