/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.adapters;

import com.arsdigita.db.DbHelper;
import com.arsdigita.db.OraLob;
import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;


/**
 * StringAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/09 $
 **/

public class StringAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/StringAd.java#2 $ by $Author: vadim $, $DateTime: 2004/07/09 13:33:10 $";

    public StringAd() {
	super("global.String", Types.VARCHAR);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setString(index, (String) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        if (md.getColumnType(rs.findColumn(column)) == Types.CLOB &&
            DbHelper.getDatabase(rs) != DbHelper.DB_POSTGRES) {
            Clob clob = rs.getClob(column);
            if (clob == null) {
                return null;
            } else {
                return clob.getSubString(1L, (int)clob.length());
            }
        } else {
            return rs.getString(column);
        }
    }

    public boolean isMutation(Object value, int jdbcType) {
        return (value != null && jdbcType == Types.CLOB);
    }

    public void mutate(ResultSet rs, String column, Object value, int jdbcType)
        throws SQLException {
        if (DbHelper.getDatabase(rs) == DbHelper.DB_POSTGRES) {
            // do nothing
            return;
        }

        Writer out = OraLob.getCharacterOutputStream(rs.getClob(column));
        try {
            out.write(((String) value).toCharArray());
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
