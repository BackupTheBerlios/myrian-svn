/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.db;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

import org.apache.log4j.Category;

public class Initializer
    implements com.arsdigita.initializer.Initializer {

    private static final Category s_log =
         Category.getInstance(Initializer.class);

    // The next set of variables are used to hold a reference to the
    // type of database being used.
    public static final int NOT_SPECIFIED = 0;
    public static final int ORACLE = 1;
    public static final int POSTGRES = 2;
    private static int m_database = NOT_SPECIFIED;

    private Configuration m_conf = new Configuration();

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Initializer.java#5 $ by $Author: randyg $, $DateTime: 2002/08/12 07:48:16 $";

    public Initializer() throws InitializationException {
        m_conf.initParameter("jdbcUrl", 
                             "The JDBC URL for the database.",
                             String.class,
			     "jdbc:oracle:oci8:@");
        m_conf.initParameter("dbUsername",
                             "The username to use for the JDBC URL.",
                             String.class);
        m_conf.initParameter("dbPassword",
                             "The password to use for the JDBC URL.",
                             String.class);
        m_conf.initParameter("DatabaseConnectionPool",
                             "DatabaseConnectionPool implementation used for" +
                             "talking to the database.",
                             String.class);
        m_conf.initParameter("SequenceImpl",
                             "SequenceImpl implementation used by Sequences.",
                             String.class);
        m_conf.initParameter("DbExceptionHandlerImpl",
                             "DbExceptionHandler implementation used for" +
                             "parsing errors from the database.",
                             String.class);
        m_conf.initParameter("connectionRetryLimit",
                             "Number of times ConnectionManager should retry " +
                             "getting a connection.",
                             Integer.class);
        m_conf.initParameter("connectionRetrySleep",
                             "Number of ms ConnectionManager should sleep " +
                             "between retries when getting a connection.",
                             Integer.class);
        m_conf.initParameter("connectionPoolSize",
                             "Number of connections to be used by pool " +
                             "(ignored for non-pooling connection managers).",
                             Integer.class);
        m_conf.initParameter("driverSpecificParam1",
                             "An optional driver-specific variable.  " +
                             "Meaning is determined by the particular " +
                             "connection pool in use.",
                             String.class);
        m_conf.initParameter("useFixForOracle901",
                             "Turns on a fix specific to oracle 9.0.1. Corrects qctcte1 bug",
                             String.class);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        s_log.warn("Database Initializer starting...");
        String dbPool = (String)m_conf.getParameter("DatabaseConnectionPool");
        String sImpl  = (String)m_conf.getParameter("SequenceImpl");
        String dbErrorHandler = 
            (String)m_conf.getParameter("DbExceptionHandlerImpl");
        Integer retryLimit = 
            (Integer)m_conf.getParameter("connectionRetryLimit");
        Integer retrySleep = 
            (Integer)m_conf.getParameter("connectionRetrySleep");
        Integer maxConnections = 
            (Integer)m_conf.getParameter("connectionPoolSize");
        String driverSpecificParam1 = 
            (String)m_conf.getParameter("driverSpecificParam1");

        String useFixForOracle901 = (String) m_conf.getParameter("useFixForOracle901");

        try {
            SQLExceptionHandler.setDbExceptionHandlerImplName(dbErrorHandler);
        } catch (Exception e) {
            s_log.error("Error setting up exception handler", e);
            throw new InitializationException(
                "Error setting DbExceptionHandlerImpl", e);
        }

        if ( dbPool == null ) {
            dbPool = "com.arsdigita.db.oracle.OracleConnectionPoolImpl";
        }


        if ( sImpl == null ) {
            sImpl = "com.arsdigita.db.oracle.OracleSequenceImpl";
        }

        setDatabase(dbPool);

        if (null != useFixForOracle901 &&  useFixForOracle901.equals("true") &&
            getDatabase() == ORACLE) {
            com.arsdigita.db.oracle.OracleConnectionPoolImpl.setUseFixFor901(true);
        }

        if (retryLimit == null) {
            retryLimit = new Integer(0);
        }
        
        if (retrySleep == null) {
            retrySleep = new Integer(100);
        }

        if (maxConnections == null) {
            maxConnections = new Integer(8);
        }

        // driverSpecificParam1 may be null.

        Sequences.setSequenceImplName(sImpl);
        ConnectionManager.setDatabaseConnectionPoolName(dbPool);
        ConnectionManager.setRetryLimit(retryLimit.intValue());
        ConnectionManager.setRetrySleep(retrySleep.intValue());
        ConnectionManager.setConnectionPoolSize(maxConnections.intValue());

        s_log.info("Setting ConnectionManager default connection info...");

        try {									
            ConnectionManager.setDefaultConnectionInfo(
                (String) m_conf.getParameter("jdbcUrl"),
                (String) m_conf.getParameter("dbUsername"),
                (String) m_conf.getParameter("dbPassword"));
        } catch (java.sql.SQLException e) {
            throw new InitializationException(
                "SQLException initializing " +
                "dbapi " + e.getMessage());
        }

        if (driverSpecificParam1 != null) {
            ConnectionManager.setDriverSpecificParameter(
                "param1", driverSpecificParam1);
        }

        s_log.warn("Database initializer finished.");

    }

    public void shutdown() {
		ConnectionManager.freeConnections();
	}


    /**
     *  @param className This is the string representation of a class that
     *  can be used to determine the type of database.  For instance,
     *  com.arsdigita.oracle.* or com.arsdigita.postgres.* both work.
     */
    private void setDatabase(String className) {
        if (className == null) {
            setDatabase(NOT_SPECIFIED);
        } else if (className.toLowerCase().indexOf("oracle") > -1) {
            setDatabase(ORACLE);
        } else if (className.toLowerCase().indexOf("postgres") > -1) {
            setDatabase(POSTGRES);
        } else {
            setDatabase(NOT_SPECIFIED);
        }
    }


    /**
     *  This will return the type of database that is being used by
     *  the system.  It will return NOT_SPECIFIED if no database has been
     *  specified.  Otherwise, it will return an int corresponding to
     *  one of the database constants defined in this file.
     */
    public static int getDatabase() {
        return m_database;
    }

    /**
     *  This allows external sources to set the database so that it
     *  is possible to set it from the command line
     */
    public static void setDatabase(int database) {
        m_database = database;
    }
}
