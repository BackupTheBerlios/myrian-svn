/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.pdl.adapters;

import com.arsdigita.util.AssertionError;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;


/**
 * DateAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/07/20 $
 **/

public class DateAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/DateAd.java#2 $ by $Author: ashah $, $DateTime: 2004/07/20 14:54:36 $";

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
