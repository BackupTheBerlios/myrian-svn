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

package com.arsdigita.db.oracle;

import com.arsdigita.db.DbExceptionHandlerBaseImpl;



/**
 * Class for processing of Oracle DB Exceptions.
 *
 * @author David Eison
 * @version $Revision: #4 $
 * @since 4.6
 */
public class OracleDbExceptionHandlerImpl extends DbExceptionHandlerBaseImpl {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/db/oracle/OracleDbExceptionHandlerImpl.java#4 $";

    static {
        // lots of stuff can keep us away from the DB...
        errors.put("ORA-01034",
                   com.arsdigita.db.DbNotAvailableException.class);
        // initialization or shutdown in progress
        errors.put("ORA-01033",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("No suitable driver",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("Invalid Oracle URL specified",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("ORA-12154",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("not connected",
                   com.arsdigita.db.DbNotAvailableException.class);
        // your session has been killed
        errors.put("ORA-00028",
                   com.arsdigita.db.DbNotAvailableException.class);
        // not logged on
        errors.put("ORA-01012",
                   com.arsdigita.db.DbNotAvailableException.class);
        // end-of-file on communication channel
        errors.put("ORA-03113",
                   com.arsdigita.db.DbNotAvailableException.class);
        // not connected to Oracle
        errors.put("ORA-03114",
                   com.arsdigita.db.DbNotAvailableException.class);
        // TNS:packet writer failure
        errors.put("ORA-12571",
                   com.arsdigita.db.DbNotAvailableException.class);
        // immediate shutdown in progress
        errors.put("ORA-01089",
                   com.arsdigita.db.DbNotAvailableException.class);
        errors.put("Closed Connection",
                   com.arsdigita.db.DbNotAvailableException.class);
        // TNS:listener could not resolve SERVICE_NAME given in connect descriptor
        errors.put("ORA-12514",
                   com.arsdigita.db.DbNotAvailableException.class);
        // TNS:listener could not find available handler with matching protocol stack
        errors.put("ORA-12516",
                   com.arsdigita.db.DbNotAvailableException.class);
        // TNS:listener could not find instance appropriate for the client connection
        errors.put("ORA-12523",
                   com.arsdigita.db.DbNotAvailableException.class);

        // this one is pretty specific, thankfully.
        errors.put("ORA-00001",
                   com.arsdigita.db.UniqueConstraintException.class);
    }

}
