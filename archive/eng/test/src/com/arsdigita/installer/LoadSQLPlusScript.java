/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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

    public static final String versionId = "$Id: //eng/persistence/dev/test/src/com/arsdigita/installer/LoadSQLPlusScript.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
