package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;

import java.sql.*;

/**
 * ObjectAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class ObjectAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/ObjectAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

    public ObjectAd() {
	super(Root.getRoot().getObjectType("global.Object"), Types.INTEGER);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setObject(index, obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	return rs.getObject(column);
    }

}
