package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import com.arsdigita.db.DbHelper;
import java.sql.*;
import java.io.*;


/**
 * BlobAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class BlobAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/BlobAd.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    public BlobAd() {
	super(Root.getRoot().getObjectType("global.Blob"), Types.BLOB);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setBinaryStream(index, 
			   new ByteArrayInputStream((byte[])obj), 
			   ((byte[])obj).length);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
            return rs.getBytes(column);
        } else {
            Blob blob = rs.getBlob(column);
            if (blob == null) {
                return null;
            } else {
                return blob.getBytes(1L, (int)blob.length());
            }
        }
    }

    public boolean isMutation(Object value, int jdbcType) {
        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
            return false;
        } else {
            return (value != null && jdbcType == Types.BLOB);
        }
    }

    public void mutate(ResultSet rs, String column, Object value, int jdbcType)
        throws SQLException {
        oracle.sql.BLOB blob =
            (oracle.sql.BLOB) rs.getBlob(column);
        OutputStream out = blob.getBinaryOutputStream();
        try {
            out.write((byte[]) value);
            out.flush();
            out.close();
        } catch (IOException e) {
            // This used to be a persistence exception, but using
            // persistence exception here breaks ant verify-pdl
            // because the classpath isn't set up to include
            // com.arsdigita.util.*
            throw new Error("Unable to write LOB: " + e);
        }
    }

}
