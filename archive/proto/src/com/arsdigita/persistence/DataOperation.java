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

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.DataOperationType;
import com.arsdigita.persistence.metadata.Operation;
import java.util.Iterator;
import java.util.ArrayList;
import java.sql.SQLException;
import com.arsdigita.db.CallableStatement;
import org.apache.log4j.Logger;


/**
 * Used to allow the user to control execution of a named DML event (a
 * data operation, in PDL).
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @since 4.5
 * @version $Id: //core-platform/proto/src/com/arsdigita/persistence/DataOperation.java#2 $
 */
public class DataOperation {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataOperation.java#2 $ by $Author: rhs $, $DateTime: 2003/01/09 18:20:28 $";

    private static final Logger s_cat =
        Logger.getLogger(DataOperation.class);

    private Session m_session;
    private DataOperationType m_type;


    /**
     * Creates a new data operation to run within a particular session.
     *
     * @deprecated this constructor will eventually be made protected since
     *             the "set" methods are also protected.
     * @param session the session to get a connection from
     */
    DataOperation(Session session, DataOperationType type) {
        m_session = session;
        m_type = type;
    }


    /**
     * Executes the query.  If this is a "callable" event, that is
     * the user expects results back from the event, only one
     * operation ("do" block) can be declared.  If more than that
     * are declared, they are all executed but the results are
     * only available for the last one.
     */
    public void execute() {
        throw new Error("not implemented");
    }


    /**
     * Explicitly closes this DataOperation if it was used to
     * execute a PL/SQL function (CallableStatement).
     * It should be called after your program is finished calling
     * {@link #get(String parameterName)}
     */
    public synchronized void close() {
        throw new Error("not implemented");
    }


    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    /**
     *  This method allows developers that are using PL/SQL as
     *  functions and procedures that return values to
     *  retrieve those valuse after calling {@link #execute()}
     *
     *  @param parameterName The name of the parameter to retrieve
     */
    public Object get(String parameterName) {
        throw new Error("not implemented");
    }

    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    public void setParameter(String parameterName, Object value) {
        throw new Error("not implemented");
    }


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName) {
        throw new Error("not implemented");
    }

    public String toString() {
        return "DataOperation: " + Utilities.LINE_BREAK + " + " +
            "Type = " + m_type + Utilities.LINE_BREAK;
    }
}
