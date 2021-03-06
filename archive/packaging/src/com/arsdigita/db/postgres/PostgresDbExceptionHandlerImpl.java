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

package com.arsdigita.db.postgres;

import com.arsdigita.db.DbExceptionHandlerBaseImpl;



/**
 * Class for processing of Postgres DB Exceptions.
 *
 * @author David Eison
 * @version $Revision: #2 $
 * @since 4.6
 */
public class PostgresDbExceptionHandlerImpl extends DbExceptionHandlerBaseImpl {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/db/postgres/PostgresDbExceptionHandlerImpl.java#2 $";

    static {
        errors.put("Cannot insert a duplicate key into unique index",
                   com.arsdigita.db.UniqueConstraintException.class);

        // lots of stuff can keep us away from the DB...
        // TODO: Expand this list
        errors.put("Connection refused.",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("Something unusual has occured to cause the driver to fail.",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("No suitable driver",
                   com.arsdigita.db.DbNotAvailableException.class);

        errors.put("The user property is missing",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("This connection has been terminated by the administrator.",
                   com.arsdigita.db.DbNotAvailableException.class);

       errors.put("FATAL 1:  This connection has been terminated by the administrator.",
                  com.arsdigita.db.DbNotAvailableException.class);
       errors.put("Broken pipe",
                  com.arsdigita.db.DbNotAvailableException.class);

        // These errors are here so that the PersistenceExceptionTest passes
        // on both oracle and postgres. They can also theoretically occur in
        // postgres mode if you forget to switch your jdbc URL from the oracle
        // version.
        errors.put("Invalid Oracle URL specified",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("ORA-12154",
                   com.arsdigita.db.DbNotAvailableException.class);
    }

}
