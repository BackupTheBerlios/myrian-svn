/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.pdl.adapters;

import com.arsdigita.db.DbHelper;
import com.arsdigita.db.OraLob;
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
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class BlobAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/BlobAd.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
            // com.arsdigita.util.*
            throw new Error("Unable to write LOB: " + e);
        }
    }

}
