/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.arsdigita.persistence;

import com.redhat.persistence.ProtoException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.HashMap;
import org.apache.log4j.Logger;


/**
 * Used to allow the user to control execution of a named DML event (a
 * data operation, in PDL).
 *
 * @author Patrick McNeill
 * @since 4.5
 * @version $Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataOperation.java#2 $
 */
public class DataOperation {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataOperation.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
