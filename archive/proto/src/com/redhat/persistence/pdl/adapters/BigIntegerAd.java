package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.math.*;
import java.sql.*;

/**
 * BigIntegerAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class BigIntegerAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/BigIntegerAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
