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

import org.apache.log4j.Logger;
import com.arsdigita.db.postgres.*;
import com.arsdigita.db.oracle.*;

public class Initializer
    implements com.arsdigita.initializer.Initializer {

    private static final Logger s_log =
        Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Initializer.java#7 $ by $Author: dan $, $DateTime: 2002/08/14 05:45:56 $";

    public static final String JDBC_URL = "jdbcUrl";
    public static final String DB_USERNAME = "dbUsername";
    public static final String DB_PASSWORD = "dbPassword";
    public static final String CONNECTION_RETRY_LIMIT = "connectionRetryLimit";
    public static final String CONNECTION_RETRY_SLEEP = "connectionRetrySleep";
    public static final String CONNECTION_POOL_SIZE = "connectionPoolSize";
    public static final String DRIVER_SPECIFIC_PARAM1 = "driverSpecificParam1";
    public static final String USE_FIX_FOR_ORACLE_901 = "useFixForOracle901";

    public Initializer() throws InitializationException {
        m_conf.initParameter(JDBC_URL,
                             "The JDBC URL for the database.",
                             String.class,
                             "jdbc:oracle:oci8:@");
        m_conf.initParameter(DB_USERNAME,
                             "The username to use for the JDBC URL.",
                             String.class);
        m_conf.initParameter(DB_PASSWORD,
                             "The password to use for the JDBC URL.",
                             String.class);
        m_conf.initParameter(CONNECTION_RETRY_LIMIT,
                             "Number of times ConnectionManager should retry " +
                             "getting a connection.",
                             Integer.class);
        m_conf.initParameter(CONNECTION_RETRY_SLEEP,
                             "Number of ms ConnectionManager should sleep " +
                             "between retries when getting a connection.",
                             Integer.class);
        m_conf.initParameter(CONNECTION_POOL_SIZE,
                             "Number of connections to be used by pool " +
                             "(ignored for non-pooling connection managers).",
                             Integer.class);
        m_conf.initParameter(DRIVER_SPECIFIC_PARAM1,
                             "An optional driver-specific variable.  " +
                             "Meaning is determined by the particular " +
                             "connection pool in use.",
                             String.class);
        m_conf.initParameter(USE_FIX_FOR_ORACLE_901,
                             "Turns on a fix specific to oracle 9.0.1. " +
                             "Corrects qctcte1 bug",
                             String.class);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }

    public void startup() {
        s_log.warn("Database Initializer starting...");

        String jdbcUrl = (String)m_conf.getParameter(JDBC_URL);
        int database = DbHelper.getDatabaseFromURL(jdbcUrl);
        DbHelper.setDatabase(database);


        try {
            if (database == DbHelper.DB_ORACLE) {
                SQLExceptionHandler.setDbExceptionHandlerImplName(
                    OracleDbExceptionHandlerImpl.class.getName()
                );
            } else if (database == DbHelper.DB_POSTGRES) {
                SQLExceptionHandler.setDbExceptionHandlerImplName(
                    PostgresDbExceptionHandlerImpl.class.getName()
                );
            } else { 
                DbHelper.unsupportedDatabaseError("SQL Exception Handler");
            }
        } catch (Exception ex) {
            throw new InitializationException(
                "Cannot set database exception handler", ex
            );
        }



        if (database == DbHelper.DB_ORACLE) {
            Sequences.setSequenceImplName(
                OracleSequenceImpl.class.getName()
            );
        } else if (database == DbHelper.DB_POSTGRES) {
            Sequences.setSequenceImplName(
                PostgresSequenceImpl.class.getName()
            );
        } else {
            DbHelper.unsupportedDatabaseError("Sequences");
        }


        if (database == DbHelper.DB_ORACLE) {
            ConnectionManager.setDatabaseConnectionPoolName(
                OracleConnectionPoolImpl.class.getName()
            );
        } else if (database == DbHelper.DB_POSTGRES) {
            ConnectionManager.setDatabaseConnectionPoolName(
                PostgresConnectionPoolImpl.class.getName()
            );
        } else {
            DbHelper.unsupportedDatabaseError("Connection Pool");
        }


        if (DbHelper.getDatabase() == DbHelper.DB_ORACLE) {
            String useFixForOracle901 = (String) m_conf.getParameter(
                USE_FIX_FOR_ORACLE_901
            );

            if (null != useFixForOracle901 &&  
                useFixForOracle901.equals("true")) {
                com.arsdigita.db.oracle.OracleConnectionPoolImpl.setUseFixFor901(true);
            }
        }

        Integer retryLimit =
            (Integer)m_conf.getParameter(CONNECTION_RETRY_LIMIT);
        if (retryLimit == null) {
            retryLimit = new Integer(0);
        }

        Integer retrySleep =
            (Integer)m_conf.getParameter(CONNECTION_RETRY_SLEEP);
        if (retrySleep == null) {
            retrySleep = new Integer(100);
        }

        Integer maxConnections =
            (Integer)m_conf.getParameter(CONNECTION_POOL_SIZE);
        if (maxConnections == null) {
            maxConnections = new Integer(8);
        }

        ConnectionManager.setRetryLimit(retryLimit.intValue());
        ConnectionManager.setRetrySleep(retrySleep.intValue());
        ConnectionManager.setConnectionPoolSize(maxConnections.intValue());

        s_log.info("Setting ConnectionManager default connection info...");
        
        String dbUsername = (String)m_conf.getParameter(DB_USERNAME);
        String dbPassword = (String)m_conf.getParameter(DB_PASSWORD);
        try {
            ConnectionManager.setDefaultConnectionInfo(
                jdbcUrl, dbUsername, dbPassword
            );
        } catch (java.sql.SQLException e) {
            throw new InitializationException(
                "SQLException initializing " +
                "dbapi " + e.getMessage());
        }

        String driverSpecificParam1 =
            (String)m_conf.getParameter(DRIVER_SPECIFIC_PARAM1);
        if (driverSpecificParam1 != null) {
            ConnectionManager.setDriverSpecificParameter(
                "param1", driverSpecificParam1);
        }

        s_log.warn("Database initializer finished.");

    }

    public void shutdown() {
        ConnectionManager.freeConnections();
    }
}
