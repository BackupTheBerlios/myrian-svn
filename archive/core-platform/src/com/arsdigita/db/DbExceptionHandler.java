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


import java.sql.SQLException;


/**
 * Interface for processing of DB Exceptions.
 * Should be implemented by database-specific subclasses.
 * Can convert an existing SQLException to a more-specific type,
 * or create a new SQLException of the correct specific type based
 * on a provided message.
 *
 * @author David Eison
 * @version $Revision: #6 $
 * @since 4.6
 */
public interface DbExceptionHandler {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DbExceptionHandler.java#6 $";

    /**
     * This method wraps the given SQLException in a more-specific
     * SQLException  (subclass of com.arsdigita.db.DbException) if one is
     * available.
     *
     * @param e The SQLException to process.
     **/

    public SQLException wrap(SQLException e);

}
