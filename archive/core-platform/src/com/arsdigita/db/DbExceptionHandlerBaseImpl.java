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

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.SequenceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Class for processing of DB Exceptions.
 * Should be subclassed with database-specific initialization.
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 * @version $Revision: #4 $
 * @since 4.6
 */
public abstract class DbExceptionHandlerBaseImpl implements DbExceptionHandler {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DbExceptionHandlerBaseImpl.java#4 $";

    private static final Logger s_cat = Logger.getLogger(DbExceptionHandlerBaseImpl.class.getName());

    /**
     * This hashmap should contain strings identifying the error, and
     * Class objects identifying the subclass to be used when that error
     * is found.
     * The Class objects should contain a constructor that
     * accepts a SQLException.  If they do not, there will be a runtime
     * error when the particular exception is encountered.
     *
     * e.g. 'errors.add("ORA-01034",
     *                  com.arsdigita.db.DbNotAvailableException.class);'
     */
    protected static final HashMap errors = new HashMap();

    /**
     * Returns the Class object representing the correct exception type for the
     * given msg, or null if no special type is associated with the msg.
     */
    protected static final Class getExceptionClass(String msg) {
        Iterator i = errors.keySet().iterator();
        while (i.hasNext()) {
            String s = (String)i.next();
            if (msg.indexOf(s) >= 0) {
                return ((Class)errors.get(s));
            }
        }
        return null;
    }

    /**
     * This method throws a more-specific SQLException
     * (subclass of com.arsdigita.db.DbException) if one is available.
     *
     * @param e The SQLException to process.
     * @throws SQLException The passed-in SQLException, re-created as a more
     *         specific type if possible.
     */
    public void throwSQLException(SQLException e) throws SQLException {
        // TODO: See if Oracle provides a better API for identifying error #?
        Class c = getExceptionClass(e.getMessage());
        if (c != null) {
            try {
                DbException newException = (DbException)(c.newInstance());
                newException.setRootCause(e);
                throw newException;
            } catch (InstantiationException err) {
                s_cat.warn("InstantiationException throwing DbException " +
                           c + ", throwing SQLException instead.");
                throw e;
            } catch (IllegalAccessException err) {
                s_cat.warn("IllegalAccessException throwing DbException " +
                           c + ", throwing SQLException instead.");
                throw e;
            }
        } else {
            // no specific error type found
            throw e;
        }
    }

    /**
     * This method throws a new SQLException, or a specific subtype
     * if one is available for the specified message.
     *
     * @param msg The message for the new SQLException.
     * @throws SQLException with the passed-in msg.
     */
    public void throwSQLException(String msg) throws SQLException {
        Class c = getExceptionClass(msg);
        if (c != null) {
            try {
                DbException newException = (DbException)(c.newInstance());
                newException.setRootCause(msg);
                throw newException;
            } catch (InstantiationException err) {
                s_cat.warn("InstantiationException throwing DbException " +
                           c + ", throwing SQLException instead.");
                throw new SQLException(msg);
            } catch (IllegalAccessException err) {
                s_cat.warn("IllegalAccessException throwing DbException " +
                           c + ", throwing SQLException instead.");
                throw new SQLException(msg);
            }
        } else {
            // no specific error type found
            throw new SQLException(msg);
        }
    }
}
