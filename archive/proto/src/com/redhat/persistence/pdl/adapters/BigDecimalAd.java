package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import java.math.*;
import java.sql.*;

/**
 * BigDecimalAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class BigDecimalAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/BigDecimalAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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
