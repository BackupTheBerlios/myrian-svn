/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.pdl.adapters;

import org.myrian.db.DbHelper;
import org.myrian.db.OraLob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * BlobAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class BlobAd extends SimpleAdapter {


    BlobAd(String type) {
        super(type, Types.BLOB);
    }

    public BlobAd() {
	this("global.Blob");
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setBinaryStream(index,
			   new ByteArrayInputStream((byte[])obj),
			   ((byte[])obj).length);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        if (DbHelper.getDatabase(rs) == DbHelper.DB_POSTGRES) {
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
        return (value != null && jdbcType == Types.BLOB);
    }

    public void mutate(ResultSet rs, String column, Object value, int jdbcType)
        throws SQLException {
        if (DbHelper.getDatabase(rs) == DbHelper.DB_POSTGRES) {
            // do nothing;
            return;
        }

        OutputStream out = OraLob.getBinaryOutputStream(rs.getBlob(column));
        try {
            out.write((byte[]) value);
            out.flush();
            out.close();
        } catch (IOException e) {
            // This used to be a persistence exception, but using
            // persistence exception here breaks ant verify-pdl
            // because the classpath isn't set up to include
            // org.myrian.util.*
            throw new Error("Unable to write LOB: " + e);
        }
    }

}
