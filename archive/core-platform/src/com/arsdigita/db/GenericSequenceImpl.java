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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * A generic Sequence implementation appropriate for use with databases
 * that don't support Oracle style sequences.
 *
 * @author <A HREF="mailto:kevin@arsdigita.com">Kevin Scaldeferri</A>
 */


public class GenericSequenceImpl extends SequenceImpl {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/GenericSequenceImpl.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /* This is a pseudo-Singleton implementation.  We create a 
     * single instance for each sequence.
     */

    // keeps a reference to each known sequence indexed by
    // sequence name
    private static HashMap s_knownSequences = new HashMap();

    private static final String currentValueSelect = 
        "select value from sequences where sequence_name = ?";

    private static final String currentValueUpdate = 
        "update sequences set value = ? where sequence_name = ?";
    // each instance has a name

    private String m_sequenceName;

    // private constructor

    private GenericSequenceImpl(String sequenceName) {
        m_sequenceName = sequenceName;
    }


    /**
     * This creation method should be used to obtain instances
     * of GenericSequenceImpl.
     */
    

    public static GenericSequenceImpl createSequence(String sequenceName) {
        GenericSequenceImpl gs;

        synchronized (s_knownSequences) {
            if (! s_knownSequences.containsKey(sequenceName)) {
                gs = new GenericSequenceImpl(sequenceName);
                s_knownSequences.put(sequenceName, gs);
            } else {
                gs = (GenericSequenceImpl) s_knownSequences.get(sequenceName);
            }
        }

        return gs;
    }

    /**
     * Gets the next value in the sequence
     */
    
    /*
     * Assumes that we have a table "sequences" in the database that
     * we use to store the currently value of a sequence
     */

    public BigDecimal getNextValue() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        try {
            BigDecimal result = this.getNextValue(conn);
            return result;
        } finally {
            conn.close();
        }
    }

    public BigDecimal getCurrentValue() throws SQLException {
        Connection conn = ConnectionManager.getConnection();
        try {
            BigDecimal result = this.getCurrentValue(conn);
            return result;
        } finally {
            conn.close();
        }
    }

    public synchronized BigDecimal getNextValue(java.sql.Connection conn) 
        throws SQLException {

        // TODO: should lock the table (in some database agnostic way)
        // need to figure out if "select ... for update" is widely supported

        BigDecimal value;

        PreparedStatement stmt = conn.prepareStatement(currentValueSelect);
        try {
            stmt.setString(1, m_sequenceName);

            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    value = rs.getBigDecimal("value");
                } else {
                    throw new DbException("Sequence " + m_sequenceName
                                          + " does not exist");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

        // increment
        value = value.add(new BigDecimal(1));

        // update the db

        PreparedStatement stmt2 = conn.prepareStatement(currentValueUpdate);
        try {
            stmt2.setBigDecimal(1, value);
            stmt2.setString(2, m_sequenceName);
            
            stmt2.executeUpdate();
            // TODO: unlock the table
        } finally {
            stmt2.close();
        }

        return value;
    }

    public synchronized BigDecimal getCurrentValue(java.sql.Connection conn) 
        throws SQLException {

        PreparedStatement stmt = conn.prepareStatement(currentValueSelect);
        try {
            stmt.setString(1, m_sequenceName);

            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return rs.getBigDecimal("value");
                } else {
                    throw new DbException("Sequence " + m_sequenceName 
                                          + " does not exist");
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }
}
