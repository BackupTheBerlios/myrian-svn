package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;

import java.sql.*;

/**
 * ObjectAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/14 $
 **/

public class ObjectAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/ObjectAd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

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
