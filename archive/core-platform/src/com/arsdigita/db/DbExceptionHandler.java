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

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.SequenceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


/**
 * Interface for processing of DB Exceptions.
 * Should be implemented by database-specific subclasses.
 * Can convert an existing SQLException to a more-specific type,
 * or create a new SQLException of the correct specific type based
 * on a provided message.
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 * @version $Revision: #1 $
 * @since 4.6
 */
public interface DbExceptionHandler {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DbExceptionHandler.java#1 $";

    /**
     * This method throws a more-specific SQLException 
     * (subclass of com.arsdigita.db.DbException) if one is available.
     *
     * @param e The SQLException to process.
     * @throws SQLException The passed-in SQLException, re-created as a more 
     *         specific type if possible.
     */
    public void throwSQLException(SQLException e) throws SQLException;
    
    /**
     * This method throws a new SQLException, or a specific subtype 
     * if one is available for the specified message.
     *
     * @param msg The message for the new SQLException.
     * @throws SQLException with the passed-in msg.
     */
    public void throwSQLException(String msg) throws SQLException;
}
