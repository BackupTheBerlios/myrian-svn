package com.arsdigita.persistence.proto.pdl.adapters;

import com.arsdigita.persistence.proto.metadata.*;
import java.sql.*;

/**
 * BlobAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/05/07 $
 **/

public class BlobAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/adapters/BlobAd.java#2 $ by $Author: rhs $, $DateTime: 2003/05/07 09:50:14 $";

    public BlobAd() {
	super(Root.getRoot().getObjectType("global.Blob"), Types.BLOB);
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
