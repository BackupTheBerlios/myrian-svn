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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * the Sequence class provides functionality akin to Oracle sequences,
 * i.e. unique integer values appropriate for use as primary keys
 *
 * the Sequence class does not actually provide an implementation.
 * the database dependent implementation must be implemented elsewhere
 *
 * The thread's current connection will be used for the sequence,
 * unless one does not exist in which case a new connection will be
 * retrieved and closed by the specific implementation class.
 *
 * @author Kevin Scaldeferri
 */

public class Sequences {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/db/Sequences.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private static final Logger s_log =
        Logger.getLogger(Sequences.class);

    private static String implName =
        "com.arsdigita.db.oracle.OracleSequenceImpl";

    private static final String defaultSequenceName = "acs_object_id_seq";

    private static SequenceImpl getSequenceImpl(String sequenceName) {
        SequenceImpl seq = null;

        try {
            Method m = Class.forName(implName).getMethod("createSequence",
                                                         new Class[] {String.class});

            seq = (SequenceImpl) m.invoke(null, new Object[] {sequenceName});
        } catch (Exception e) {
            s_log.warn(e);
        }

        return seq;
    }

    protected static void setSequenceImplName(String impName) {
        implName = impName;
    }

    public static BigDecimal getCurrentValue() throws SQLException {
        return getCurrentValue(defaultSequenceName);
    }

    public static BigDecimal getNextValue() throws SQLException {
        return getNextValue(defaultSequenceName);
    }

    public static BigDecimal getCurrentValue(String sequenceName)
        throws SQLException {

        java.sql.Connection conn = ConnectionManager.getCurrentThreadConnection();
        if (conn == null) {
            return getSequenceImpl(sequenceName).getCurrentValue();
        } else {
            return getNextValue(sequenceName, conn);
        }
    }

    public static BigDecimal getNextValue(String sequenceName)
        throws SQLException {
        java.sql.Connection conn = ConnectionManager.getCurrentThreadConnection();
        if (conn == null) {
            return getSequenceImpl(sequenceName).getNextValue();
        } else {
            return getNextValue(sequenceName, conn);
        }
    }

    public static BigDecimal getCurrentValue(java.sql.Connection conn)
        throws SQLException {
        return getCurrentValue(defaultSequenceName, conn);
    }

    public static BigDecimal getNextValue(java.sql.Connection conn)
        throws SQLException {
        return getNextValue(defaultSequenceName,conn);
    }

    public static BigDecimal getCurrentValue(String sequenceName,
                                             java.sql.Connection conn)
        throws SQLException {

        return getSequenceImpl(sequenceName).getCurrentValue(conn);
    }

    public static BigDecimal getNextValue(String sequenceName,
                                          java.sql.Connection conn)
        throws SQLException {
        return getSequenceImpl(sequenceName).getNextValue(conn);
    }
}
