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

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.DataOperationType;
import com.arsdigita.persistence.metadata.Operation;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;
import com.arsdigita.db.CallableStatement;


/**
 * Used to allow the user to control execution of a named DML event (a 
 * data operation, in PDL).
 * 
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @since 4.5
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/DataOperation.java#2 $
 */
public class DataOperation extends AbstractDataOperation {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/DataOperation.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    private Session m_session;
    private DataOperationType m_type;
    private CallableStatement m_callableStatement = null;

    // This is used to hold the variables, in order, for a CallableStatement
    private ArrayList variables;

    private static org.apache.log4j.Category s_cat = 
        org.apache.log4j.Category.getInstance(DataOperation.class.getName());

    /**
     * Creates a new data operation to run within a particular session.
     *
     * @deprecated this constructor will eventually be made protected since
     *             the "set" methods are also protected.
     * @param session the session to get a connection from
     */
    public DataOperation(Session session, DataOperationType type) {
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
        DataStore dataStore = m_session.getDataStore();
        for (Iterator it = m_type.getEvent().getOperations(); it.hasNext(); ) {
            Operation op = (Operation) it.next();
            variables = new ArrayList();
            if (op.isCallableStatement()) {
                m_callableStatement = dataStore.fireCallableOperation
                    (op, m_source, variables);
            } else {
                dataStore.fireOperation(op, m_source); 
                m_callableStatement = null;
                variables = null;
            }
        }
    }


    /**
     * Explicitly closes this DataOperation if it was used to
     * execute a PL/SQL function (CallableStatement).  
     * It should be called after your program is finished calling
     * {@link get(String parameterName)}
     */    
    public synchronized void close() {
        if (m_callableStatement != null) {
            try {
                // associated statement closing should be handled
                // automatically if close after use flag was set.
                m_callableStatement.close();
            } catch (SQLException e) {
                throw PersistenceException.newInstance(e);
            }
            m_callableStatement = null;
        }
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
        try {
            if (m_callableStatement != null) {
                int index = variables.indexOf(parameterName);
                if (index > -1) {
                    return m_callableStatement.getObject(index + 1);
                } else {
                    throw new PersistenceException
                        ("The variable you have requested (" + parameterName +
                         "is not found within the executed statement");
                }
            } else {
                throw new PersistenceException
                    ("You must call execute() before trying to retrieve " +
                     "values");
            }
        } catch (SQLException e) {
            throw PersistenceException.newInstance(e);
        }
    }

    public String toString() {
        return "DataOperation: " + Utilities.LINE_BREAK + " + " +
            "Type = " + m_type + Utilities.LINE_BREAK +
            "+ Bindings = " + m_source;
    }
}
