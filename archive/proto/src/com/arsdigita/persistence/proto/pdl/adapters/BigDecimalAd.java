package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.math.*;
import java.sql.*;

/**
 * BigDecimalAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class BigDecimalAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BigDecimalAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

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
