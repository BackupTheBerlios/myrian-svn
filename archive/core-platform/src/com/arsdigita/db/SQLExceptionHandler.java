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

import java.sql.SQLException;

/**
 * Class for delegating processing of DB Exceptions.
 * Can convert an existing SQLException to a more-specific type,
 * or create a new SQLException of the correct specific type based
 * on a provided message.
 *
 * It is necessary for an initializer to call 
 * setDbExceptionHandlerImplName before using this class 
 * (normally this is called via the DB Initializer).
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 * @version $Revision: #1 $
 * @since 4.6
 */
public class SQLExceptionHandler {
       
    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/SQLExceptionHandler.java#1 $";

    private static String s_exceptionHandlerName = "com.arsdigita.db.oracle.OracleDbExceptionHandlerImpl";

    private static DbExceptionHandler s_handler = null;

    public static void setDbExceptionHandlerImplName() 
            throws ClassNotFoundException, 
                   InstantiationException,
                   IllegalAccessException {
        setDbExceptionHandlerImplName(null);
    }

    /**
     * Sets the implementation to be used for DB exception parsing.
     *
     * If given a null value, uses default implementation.
     */
    public static void setDbExceptionHandlerImplName(String exceptionHandlerName) 
            throws ClassNotFoundException, 
                   InstantiationException,
                   IllegalAccessException {
        if (exceptionHandlerName != null) {
            s_exceptionHandlerName = exceptionHandlerName;
        }
        s_handler = (DbExceptionHandler)((Class.forName(s_exceptionHandlerName).newInstance()));
    }

    /**
     * This method throws a more-specific SQLException 
     * (subclass of com.arsdigita.db.DbException) if one is available.
     *
     * @throws SQLException a SQLException, re-created as a more specific
     *         type if possible.
     */
    public static void throwSQLException(SQLException e) throws SQLException {
        Assert.assertNotNull(s_handler, "DB Specific Exception Handler Class");
        s_handler.throwSQLException(e);
    }

    /**
     * This method throws a new SQLException, or a specific subtype 
     * if one is available for the specified message.
     *
     * @param msg The message for the new SQLException.
     * @throws SQLException with the passed-in msg.
     */
    public static void throwSQLException(String msg) throws SQLException {
        Assert.assertNotNull(s_handler, "DB Specific Exception Handler Class");
        s_handler.throwSQLException(msg);
    }
}


