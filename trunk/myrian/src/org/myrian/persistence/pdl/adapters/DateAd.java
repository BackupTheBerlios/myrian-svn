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
package org.myrian.persistence.pdl.adapters;

import org.myrian.util.AssertionError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;


/**
 * DateAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 **/

public class DateAd extends SimpleAdapter {


    /*
     * before Java 1.4 Timstamp#getTime() returned only the seconds value part
     * of the timestamp and not the milliseconds which are record in the nanos
     * part of the timestamp
     */
    private static final boolean s_accountForNanos;

    static {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            int minor = new Integer(version.charAt(2)).intValue();
            if (minor < 4) {
                s_accountForNanos = true;
            } else {
                s_accountForNanos = false;
            }
        } else {
            throw new Error("unsupported java version");
        }
    }

    public DateAd() {
        super("global.Date", Types.TIMESTAMP);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
        throws SQLException {

        Timestamp tstamp = null;

        // Timestamp overrides the getTime() method in a way that requires us to
        // jump through a few extra hoops here.
        if ( obj instanceof Timestamp ) {
            tstamp = (Timestamp) obj;
        } else if (obj instanceof java.util.Date) {
            tstamp = new Timestamp(((java.util.Date) obj).getTime());
        } else {
            throw new AssertionError
                ("Not a Date: " +
                 ( obj == null ? "null" : obj.getClass().getName()));
        }

        ps.setTimestamp(index, tstamp);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        Timestamp tstamp = rs.getTimestamp(column);
        if (tstamp == null) {
            return null;
        } else {
            if (s_accountForNanos) {
                return new java.util.Date
                    (tstamp.getTime() + tstamp.getNanos() / 1000000);
            } else {
                return new java.util.Date(tstamp.getTime());
            }
        }
    }
}
