package com.arsdigita.installer;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * SQLLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/11/10 $
 **/

public abstract class SQLLoader {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/SQLLoader.java#3 $ by $Author: rhs $, $DateTime: 2003/11/10 12:56:19 $";

    private static final Logger s_log = Logger.getLogger(SQLLoader.class);

    private Connection m_conn;

    public SQLLoader(Connection conn) {
        m_conn = conn;
    }

    protected abstract Reader open(String name);

    public void load(String name) {
        try {
            Statement stmt = m_conn.createStatement();
            try {
                load(stmt, name, null, name);
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void load(final Statement stmt, final String base,
                      final String from, final String name) {
	if (s_log.isInfoEnabled()) {
	    s_log.info("Loading " + name + " using base " + base);
	}

        try {
            Reader reader = open(name);
            if (reader == null) {
                throw new IllegalArgumentException
                    ("no such file: " + name +
                     (from == null ? "" : (", included from: " + from)));
            }

            StatementParser sp = new StatementParser
                (name, reader,
                 new StatementParser.Switch() {
                     public void onStatement(String sql) {
                         execute(stmt, sql);
                     }
                     public void onInclude(String include, boolean relative) {
                         if (relative) {
                             include(stmt, base, name, include);
                         } else {
                             include(stmt, base, base, include);
                         }
                     }
                 });
            sp.parse();
            reader.close();
        } catch (ParseException e) {
            throw new UncheckedWrapperException(e);
        } catch (IOException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private String parent(String path) {
        path = path.trim();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 2);
        }

        int index = path.lastIndexOf('/');
        if (index > 0) {
            path = path.substring(0, index);
        } else {
            path = null;
        }

        return path;
    }

    private void include(Statement stmt, String base, String from,
                         String included) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Resolving include: '" + included + "'");
        }

        String front = parent(from);
        String back = included;
        while (back.startsWith("../")) {
            back = back.substring(3);
            front = parent(front);
        }

        String resolved;
        if (front == null) {
            resolved = back;
        } else {
            resolved = front + "/" + back;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Recursively including: '" + resolved + "'");
        }

        load(stmt, base, from, resolved);
    }

    private void execute(Statement stmt, String sql) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Executing SQL " + sql);
        }

        try {
            int rowsAffected = stmt.executeUpdate(sql);
            if (s_log.isDebugEnabled()) {
                s_log.debug("  " + rowsAffected + " row(s) affected");
            }
        } catch (SQLException e) {
            throw new UncheckedWrapperException(sql, e);
        }
    }

}
