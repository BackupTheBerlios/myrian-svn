/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.db;


import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Category;

public class DbHelper {

    // Only static methods in this class
    private DbHelper() {}

    private static Category s_log = Category.getInstance(DbHelper.class);

    public static final int DB_DEFAULT = 0;
    public static final int DB_ORACLE = 1;
    public static final int DB_POSTGRES = 2;

    public static final int DB_MAX = DB_POSTGRES;

    private static int s_database = DB_DEFAULT;

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Sets the database type. The parameter should be one of the
     * constants specified in this file.
     */
    public static void setDatabase(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        s_database = database;

        s_log.info("Setting database to " + getDatabaseName(database));
    }

    /**
     *  This will return the type of database that is being used by
     *  the system.  It will return DB_DEFAULT if no database has been
     *  specified.  Otherwise, it will return an int corresponding to
     *  one of the database constants defined in this file.
     */
    public static int getDatabase() {
        return s_database;
    }

    /**
     * Gets the directory name for the current database
     */
    public static String getDatabaseDirectory() {
        return getDatabaseDirectory(s_database);
    }

    /**
     * Gets the directory name to be used for database specific
     * files
     */
    public static String getDatabaseDirectory(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        switch (database) {
        case DB_ORACLE:
            s_log.info("ORACLE");
            return "oracle-se";
        case DB_POSTGRES:
            s_log.info("POSTGRES");
            return "postgres";
        default:
            return "default";
        }
    }

    private static final String[] DB_SUFFIXES = { "ora", "pg" };

    /**
     * Gets the filename suffix used to distinguish between resource files
     * based on database.
     **/

    public static String getDatabaseSuffix() {
        return getDatabaseSuffix(s_database);
    }

    /**
     * Gets the filename suffix used to distinguish between resource files
     * based on database.
     **/

    public static String getDatabaseSuffix(int database) {
        switch (database) {
        case DB_ORACLE:
            return DB_SUFFIXES[0];
        case DB_POSTGRES:
            return DB_SUFFIXES[1];
        default:
            return null;
        }
    }

    public static String[] getDatabaseSuffixes() {
        return DB_SUFFIXES;
    }


    /**
     * Parses the JDBC url to determine the database
     * type, will return DB_DEFAULT if no supported
     * database is determined.
     */
    public static int getDatabaseFromURL(String url) {
        if (!url.startsWith("jdbc:")) {
            throw new IllegalArgumentException("JDBC URL " +
                                       url + " doesn't start with jdbc:");
        }

        int pos = url.indexOf(":", 5);

        if (pos == -1) {
            throw new IllegalArgumentException("JDBC URL " + url +
                                       " is not of the form jdbc:[dbname]:xyz");
        }

        String driver = url.substring(5, pos);
        s_log.info("Got driver name " + driver, new  Throwable());

        if ("oracle".equals(driver)) {
            return DB_ORACLE;
        } else if ("postgresql".equals(driver)) {
            return DB_POSTGRES;
        } else {
            return DB_DEFAULT;
        }
    }

    /**
     * Gets the pretty name for a given database integer
     * identifier.
     */
    public static String getDatabaseName(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        switch (database) {
        case DB_ORACLE:
            return "Oracle SE";
        case DB_POSTGRES:
            return "PostgreSQL";
        default:
            return "Default";
        }
    }


    /**
     * Convenience method for throwing a DbUnsupportedException
     * filling in the message for the current database type.
     */
    public static void unsupportedDatabaseError(String operation) {
        throw new DbUnsupportedException("Database " +
                                         DbHelper.getDatabaseName(s_database) +
                                         " does not support " + operation);
    }

    /**
     * Returns the width of the VARCHAR column required to store
     * <code>str</code> in the database.
     *
     * <p>This abstracts the differences in the interpretation of, say,
     * VARCHAR(100) in Oracle and Postgres. In Oracle, this means 100
     * bytes. Therefore, a 100-character long string may not fit in a
     * VARCHAR(100) column in Oracle, depending on the particular encoding used.
     * In Postgres, VARCHAR(100) means 100 characters.</p>
     *
     * @return 1 if <code>str</code> is <code>null</code>; otherwise a
     * db-specific positive value.
     **/
    public static int varcharLength(String str) {
        if ( str == null || "".equals(str) ) return 1;

        /**
         * See change 30544.  See also
         * http://post-office.corp.redhat.com/archives/ccm-engineering-list/2003-May/msg00016.html
         * (that is Message-ID: <20030502111749.GB1867@tuborg>, in case the URL
         * changes)
         * See also Dan's followup at
         * http://post-office.corp.redhat.com/archives/ccm-engineering-list/2003-May/msg00017.html
         **/
        int result = 0;
        switch (getDatabase()) {
        case DB_POSTGRES:
            result = str.length();
            break;
        case DB_ORACLE:
            try {
                result = str.getBytes(DEFAULT_ENCODING).length;
            } catch (java.io.UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException
                    (DEFAULT_ENCODING + " not supported by JRE", ex);
            }
            break;
        default:
            DbHelper.unsupportedDatabaseError("varcharLength");
        }

        if ( result == 0 ) return 1;

        return result;
    }
}
