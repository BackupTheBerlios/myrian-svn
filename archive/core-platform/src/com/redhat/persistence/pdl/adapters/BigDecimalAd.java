package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.math.*;
import java.sql.*;

/**
 * BigDecimalAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class BigDecimalAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/BigDecimalAd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public BigDecimalAd() {
	super(Root.getRoot().getObjectType("global.BigDecimal"),
              Types.NUMERIC);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        ps.setBigDecimal(index, (BigDecimal) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        return rs.getBigDecimal(column);
    }

}
