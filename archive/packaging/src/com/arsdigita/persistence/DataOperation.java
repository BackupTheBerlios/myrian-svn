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

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.SQLBlock;
import com.redhat.persistence.ProtoException;
import java.util.*;
import org.apache.log4j.Logger;


/**
 * Used to allow the user to control execution of a named DML event (a
 * data operation, in PDL).
 *
 * @author Patrick McNeill
 * @since 4.5
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataOperation.java#1 $
 */
public class DataOperation {

    public static final String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/DataOperation.java#1 $ by $Author: dennis $, $DateTime: 2003/08/14 14:53:20 $";

    private static final Logger s_cat =
        Logger.getLogger(DataOperation.class);

    private Session m_session;
    private SQLBlock m_sql;
    private HashMap m_parameters = new HashMap();


    /**
     * Creates a new data operation to run within a particular session.
     *
     * @deprecated this constructor will eventually be made protected since
     *             the "set" methods are also protected.
     * @param session the session to get a connection from
     */
    DataOperation(Session session, SQLBlock sql) {
        m_session = session;
        m_sql = sql;
    }


    /**
     * Executes the query.  If this is a "callable" event, that is
     * the user expects results back from the event, only one
     * operation ("do" block) can be declared.  If more than that
     * are declared, they are all executed but the results are
     * only available for the last one.
     */
    public void execute() {
	try {
	    m_session.getEngine().execute(m_sql, m_parameters);
	} catch (ProtoException e) {
	    throw PersistenceException.newInstance(e);
	}
    }


    /**
     * Explicitly closes this DataOperation if it was used to
     * execute a PL/SQL function (CallableStatement).
     * It should be called after your program is finished calling
     * {@link #get(String parameterName)}
     */
    public synchronized void close() {
        // do nothing
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
        m_parameters.put(Path.get(":" + parameterName), value);
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
        return m_parameters.get(Path.get(":" + parameterName));
    }

    public String toString() {
        return "DataOperation: " + m_sql;
    }
}
