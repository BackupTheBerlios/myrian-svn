package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * BlobAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/03/18 $
 **/

public class BlobAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BlobAd.java#1 $ by $Author: rhs $, $DateTime: 2003/03/18 15:44:06 $";

    public BlobAd() {
	super(Root.getRoot().getObjectType("global.Blob"));
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	byte[] bytes = (byte[]) obj;
	ps.setBytes(index, bytes);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	return rs.getBytes(column);
    }

}
