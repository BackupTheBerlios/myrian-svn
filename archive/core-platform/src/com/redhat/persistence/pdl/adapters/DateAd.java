/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
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
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public class DateAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/DateAd.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
            return new java.util.Date(tstamp.getTime() +
                                      tstamp.getNanos() / 1000000);
        }
    }
}
