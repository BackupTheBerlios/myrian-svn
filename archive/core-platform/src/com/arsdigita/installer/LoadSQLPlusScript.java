/*
* Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.installer;

import com.arsdigita.db.DbHelper;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class LoadSQLPlusScript {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/installer/LoadSQLPlusScript.java#15 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private static final Logger s_log =
            Logger.getLogger(LoadSQLPlusScript.class);

    private Connection m_con;

    public static void main (String args[]) {
        BasicConfigurator.configure();

        if (args.length != 4) {
            s_log.error("Usage: LoadSQLPlusScript " +
                    "<JDBC_URL> <username> <password> <script_filename>");
            System.exit(1);
        }

        String jdbcUrl = args[0];
        String dbUsername = args[1];
        String dbPassword = args[2];
        String scriptFilename = args[3];

        LoadSQLPlusScript loader = new LoadSQLPlusScript();
        loader.setConnection (jdbcUrl, dbUsername, dbPassword);
        loader.loadSQLPlusScript(scriptFilename);
    }

    public void setConnection (Connection connection) {
        m_con = connection;
    }

    public void setConnection (String jdbcUrl, String dbUsername,
                               String dbPassword) {
        try {
            int db = DbHelper.getDatabaseFromURL(jdbcUrl);

            switch (db) {
                case DbHelper.DB_POSTGRES:
                    Class.forName("org.postgresql.Driver");
                    break;
                case DbHelper.DB_ORACLE:
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    break;
                default:
                    throw new IllegalArgumentException("unsupported database");
            }

            s_log.warn("Using database " + DbHelper.getDatabaseName(db));
            m_con = DriverManager.getConnection(jdbcUrl, dbUsername,
                    dbPassword);
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        } catch (ClassNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public void loadSQLPlusScript (String scriptFilename) {
        loadScript(scriptFilename);
    }

    protected void loadScript(String scriptFilename) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Loading: '" + scriptFilename + "'");
        }
        SQLLoader loader = new SQLLoader(m_con) {
            protected Reader open(String name) {
                try {
                    return new FileReader(name);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        };
        loader.load(scriptFilename);
        try {
            m_con.commit();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
