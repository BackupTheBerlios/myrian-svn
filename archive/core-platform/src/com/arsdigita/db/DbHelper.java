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


import com.arsdigita.util.Assert;
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
            return "oracle-se";
        case DB_POSTGRES:
            return "postgres";
        default:
            return "default";
        }
    }

    /**
     * Parses the JDBC url to determine the database
     * type, will return DB_DEFAULT if no supported
     * database is determined.
     */
    public static int getDatabaseFromURL(String url) {
        if (!url.startsWith("jdbc:")) {
            throw new RuntimeException("JDBC URL " + 
                                       url + " doesn't start with jdbc:");
        }

        int pos = url.indexOf(":", 5);

        if (pos == -1) {
            throw new RuntimeException("JDBC URL " + url + 
                                       " is not of the form jdbc:[dbname]:xyz");
        }

        String driver = url.substring(5, pos);
        s_log.info("Got driver name " + driver);

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
}
