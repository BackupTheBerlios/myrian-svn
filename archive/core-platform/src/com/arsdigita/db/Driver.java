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

import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;


/**
 * A simple implementation of the java.sql.Driver interface that wraps
 * a "real" implementation of java.sql.Driver
 *
 * @author <a href="mthomas@arsdigita.com">Mark Thomas</a>
 * @version $Revision: #4 $ $Date: 2002/10/04 $
 * @since 4.5
 */
class Driver implements java.sql.Driver {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Driver.java#4 $ $Author: rhs $ $Date: 2002/10/04 $";

    // The Driver object we wrap
    private java.sql.Driver driver;


    // Constructor: use the "wrap" class method to create instances
    private Driver(java.sql.Driver driver) {
        this.driver = driver;
    }


    /**
     * Returns true if the driver thinks that it can open a
     * connection to the given URL.
     */
    public boolean acceptsURL(String url) throws SQLException {
        try {
            return driver.acceptsURL(url);
        } catch (SQLException e) {
            throw SQLExceptionHandler.wrap(e);
        }
    }

    /**
     * Attempts to make a database connection to the given URL.
     */
    public java.sql.Connection connect(String url, Properties info)
        throws SQLException {
        try {
            return com.arsdigita.db.Connection.wrap(driver.connect(url, info), null);
        } catch (SQLException e) {
            throw SQLExceptionHandler.wrap(e);
        }
    }

    /**
     * Gets the driver's major version number.
     */
    public int getMajorVersion() {
        return driver.getMajorVersion();
    }

    /**
     * Gets the driver's minor version number.
     */
    public int getMinorVersion() {
        return driver.getMinorVersion();
    }

    /**
     * Gets information about the possible properties for this driver.
     */
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
        throws SQLException {
        try {
            return driver.getPropertyInfo(url, info);
        } catch (SQLException e) {
            throw SQLExceptionHandler.wrap(e);
        }
    }

    /**
     *  Reports whether this driver is a genuine JDBC COMPLIANTTM driver.
     */
    public boolean jdbcCompliant() {
        return driver.jdbcCompliant();
    }

}
