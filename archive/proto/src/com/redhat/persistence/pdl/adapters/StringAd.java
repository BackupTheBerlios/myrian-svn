package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.metadata.*;
import com.arsdigita.db.DbHelper;
import java.sql.*;
import java.io.*;


/**
 * StringAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class StringAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/pdl/adapters/StringAd.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public StringAd() {
	super(Root.getRoot().getObjectType("global.String"), Types.VARCHAR);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setString(index, (String) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        if (md.getColumnType(rs.findColumn(column)) == Types.CLOB &&
            DbHelper.getDatabase() != DbHelper.DB_POSTGRES) {
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
        if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
            // do nothing
        } else {
            oracle.sql.CLOB clob =
                (oracle.sql.CLOB) rs.getClob(column);
            Writer out = clob.getCharacterOutputStream();
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

}
