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
package com.arsdigita.installer;

import org.myrian.util.Assert;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 * SQLLoader
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public abstract class SQLLoader {


    private static final Logger s_log = Logger.getLogger(SQLLoader.class);

    private Connection m_conn;

    public SQLLoader(Connection conn) {
        m_conn = conn;
    }

    public Connection getConnection() {
        return m_conn;
    }

    protected abstract Reader open(String name);

    public static void load(final Connection conn,
                            final String script) {
        if (conn == null) throw new IllegalArgumentException();
        if (script == null) throw new IllegalArgumentException();

        final SQLLoader loader = new SQLLoader(conn) {
                protected final Reader open(final String name) {
                    final ClassLoader cload = getClass().getClassLoader();
                    final InputStream is = cload.getResourceAsStream(name);

                    if (is == null) {
                        return null;
                    } else {
                        return new InputStreamReader(is);
                    }
                }
            };

        loader.load(script);
    }

    public void load(String name) {
        try {
            Statement stmt = m_conn.createStatement();
            try {
                load(stmt, name, null, name);
            } finally {
                stmt.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            stmt.execute(sql);
            if (s_log.isDebugEnabled()) {
                s_log.debug(stmt.getUpdateCount() + " row(s) affected");
            }
        } catch (SQLException e) {
            throw new RuntimeException(sql, e);
        }
    }

}
