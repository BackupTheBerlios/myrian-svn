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

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.DbExceptionHandlerBaseImpl;
import com.arsdigita.db.SequenceImpl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;


/**
 * Class for processing of Oracle DB Exceptions.
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 * @version $Revision: #5 $
 * @since 4.6
 */
public class OracleDbExceptionHandlerImpl extends DbExceptionHandlerBaseImpl {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/oracle/OracleDbExceptionHandlerImpl.java#5 $";

    static {
        // lots of stuff can keep us away from the DB...
        errors.put("ORA-01034",
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

        // this one is pretty specific, thankfully.
        errors.put("ORA-00001", com.arsdigita.db.UniqueConstraintException.class);
    }

}
