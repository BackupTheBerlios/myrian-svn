package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;

import java.sql.*;

/**
 * ObjectAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/04/04 $
 **/

public class ObjectAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/ObjectAd.java#1 $ by $Author: rhs $, $DateTime: 2003/04/04 20:45:14 $";

    public ObjectAd() {
	super(Root.getRoot().getObjectType("global.Object"));
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setObject(index, obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	return rs.getObject(column);
    }

}
