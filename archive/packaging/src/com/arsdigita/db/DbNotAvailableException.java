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
 * An exception class that is used when a unique constraint
 * violation is thrown by the database.
 *
 * @author David Eison
 * @version $Revision: #1 $
 * @since 4.6
 */

public class DbNotAvailableException extends DbException {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/db/DbNotAvailableException.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    /**
     * Default constructor.  setRootCause should be called
     * after using this constructor.
     */
    public DbNotAvailableException() {
        super();
    }

    public DbNotAvailableException(String msg) {
        super(msg);
    }

    public DbNotAvailableException(SQLException e) {
        super(e);
    }

    public DbNotAvailableException(String msg, SQLException e) {
        super(msg, e);
    }

}
