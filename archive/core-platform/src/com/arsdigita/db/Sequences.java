/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.db.oracle.OracleSequenceImpl;
import com.arsdigita.db.postgres.PostgresSequenceImpl;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.sql.Connection;
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

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/Sequences.java#8 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private static final Logger s_log =
        Logger.getLogger(Sequences.class);

    private static final String defaultSequenceName = "acs_object_id_seq";

    private static SequenceImpl getSequenceImpl(Connection conn,
                                                String sequenceName)
        throws SQLException {
        String url = conn.getMetaData().getURL();

        switch (DbHelper.getDatabaseFromURL(url)) {
        case DbHelper.DB_ORACLE:
            return OracleSequenceImpl.createSequence(sequenceName);
        case DbHelper.DB_POSTGRES:
            return PostgresSequenceImpl.createSequence(sequenceName);
        default:
            DbHelper.unsupportedDatabaseError("sequences");
            return null;
        }
    }

    public static BigDecimal getCurrentValue() throws SQLException {
        return getCurrentValue(defaultSequenceName);
    }

    public static BigDecimal getNextValue() throws SQLException {
        return getNextValue(defaultSequenceName);
    }

    public static BigDecimal getCurrentValue(String sequenceName)
        throws SQLException {

        Connection conn = SessionManager.getSession().getConnection();
        return getNextValue(sequenceName, conn);
    }

    public static BigDecimal getNextValue(String sequenceName)
        throws SQLException {
        Connection conn = SessionManager.getSession().getConnection();
        return getNextValue(sequenceName, conn);
    }

    public static BigDecimal getCurrentValue(Connection conn)
        throws SQLException {
        return getCurrentValue(defaultSequenceName, conn);
    }

    public static BigDecimal getNextValue(Connection conn)
        throws SQLException {
        return getNextValue(defaultSequenceName,conn);
    }

    public static BigDecimal getCurrentValue(String sequenceName,
                                             Connection conn)
        throws SQLException {
        return getSequenceImpl(conn, sequenceName).getCurrentValue(conn);
    }

    public static BigDecimal getNextValue(String sequenceName, Connection conn)
        throws SQLException {
        return getSequenceImpl(conn, sequenceName).getNextValue(conn);
    }
}
