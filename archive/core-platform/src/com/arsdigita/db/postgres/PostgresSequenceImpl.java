/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.db.postgres;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.SequenceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


/**
 * Implementation of the Sequence class for the Postgres RDBMS.
 *
 * @author Patrick McNeill
 */

public class PostgresSequenceImpl extends SequenceImpl {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/postgres/PostgresSequenceImpl.java#9 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private String m_sequenceName;

    // private constructor

    private PostgresSequenceImpl(String sequenceName) {
        m_sequenceName = sequenceName;
    }

    public static PostgresSequenceImpl createSequence(String sequenceName) {
        return new PostgresSequenceImpl(sequenceName);
    }

    public BigDecimal getCurrentValue(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement
            ("select currval('" + m_sequenceName + "')");
        try {
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist.");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    public BigDecimal getNextValue(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement
            ("select nextval('" + m_sequenceName + "')");
        try {
            ResultSet rs = stmt.executeQuery();

            try {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                } else {
                    throw new SQLException("Sequence " + m_sequenceName
                                           + " does not exist.");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }
}
