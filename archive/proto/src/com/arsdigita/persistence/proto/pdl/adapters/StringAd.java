package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;


/**
 * StringAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class StringAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/StringAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

    public StringAd() {
	super(Root.getRoot().getObjectType("global.String"), Types.VARCHAR);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setString(index, (String) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	return rs.getString(column);
    }

}
