package com.arsdigita.installer;

import com.arsdigita.util.UncheckedWrapperException;

import java.io.*;
import java.sql.*;

import org.apache.log4j.Logger;

/**
 * SQLLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/08/27 $
 **/

public class SQLLoader {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/installer/SQLLoader.java#1 $ by $Author: rhs $, $DateTime: 2003/08/27 19:16:25 $";

    private static final Logger s_log = Logger.getLogger(SQLLoader.class);

    private Connection m_conn;

    public SQLLoader(Connection conn) {
        m_conn = conn;
    }

    public void load(String filename) {
        try {
            Statement stmt = m_conn.createStatement();
            try {
                load(stmt, filename, filename);
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void load(final Statement stmt, final String base,
                      final String filename) {
        try {
            StatementParser sp = new StatementParser
                (filename, new FileReader(filename),
                 new StatementParser.Switch() {
                     public void onStatement(String sql) {
                         execute(stmt, sql);
                     }
                     public void onInclude(String include) {
                         include(stmt, base, filename, include);
                     }
                 });
            sp.parse();
        } catch (ParseException e) {
            throw new UncheckedWrapperException(e);
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    private void include(Statement stmt, String base, String including,
                         String included) {
        File includedFile = new File(included);
        if (includedFile.isAbsolute()) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Absolute path found: '" + included + "'");
            }
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Relative path found: '" + included + "'");
            }
            //  Well make it absolute then.
            includedFile =
                new File(new File(base).getAbsoluteFile()
                         .getParentFile(), included).getAbsoluteFile();
        }
        if (s_log.isInfoEnabled()) {
            s_log.info("Recursively including: '" + includedFile + "'");
        }
        if (!includedFile.exists()) {
            throw new IllegalStateException
                ("no such file: " + includedFile + ", included from: " +
                 base);
        }
        load(stmt, base, includedFile.toString());
    }

    private void execute(Statement stmt, String sql) {
        if (s_log.isInfoEnabled()) {
            s_log.info(sql);
        }

        try {
            int rowsAffected = stmt.executeUpdate(sql);
            if (s_log.isInfoEnabled()) {
                s_log.info("  " + rowsAffected + " row(s) affected");
            }
        } catch (SQLException e) {
            throw new UncheckedWrapperException(sql, e);
        }
    }

}
