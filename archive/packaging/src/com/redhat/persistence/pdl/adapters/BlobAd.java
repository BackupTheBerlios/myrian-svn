/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import com.arsdigita.db.DbHelper;
import java.sql.*;
import java.io.*;


/**
 * BlobAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/08/27 $
 **/

public class BlobAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/redhat/persistence/pdl/adapters/BlobAd.java#3 $ by $Author: rhs $, $DateTime: 2003/08/27 19:33:58 $";

    public BlobAd() {
	super("global.Blob", Types.BLOB);
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
