/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class DateAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/DateAd.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
