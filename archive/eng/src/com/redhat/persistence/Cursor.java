/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 **/

public class Cursor {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/Cursor.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private static final Logger s_log = Logger.getLogger(Cursor.class);

    final private DataSet m_ds;

    private RecordSet m_rs = null;
    private Map m_values = null;
    private long m_position = 0;
    private boolean m_closed = false;

    protected Cursor(DataSet ds) {
        m_ds = ds;
    }

    public DataSet getDataSet() {
        return m_ds;
    }

    public Session getSession() {
        return m_ds.getSession();
    }

    public boolean isClosed() {
	return m_closed;
    }

    private Object getInternal(Path path) {
	if (m_values.containsKey(path)) {
	    return m_values.get(path);
	} else {
	    Object o = getInternal(path.getParent());
            if (o == null) { return null; }
	    return getSession().get(o, Path.get(path.getName()));
	}
    }

    public Object get(Path path) {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        if (m_position <= 0) {
	    throw new NoRowException(this);
        }

        if (!m_rs.isFetched(path)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("path " + path + " is not fetched"
                            + " in signature " + m_ds.getSignature());
            }
            throw new NotFetchedException(this, path);
        }

        return getInternal(path);
    }

    public Object get(String path) {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        return get(Path.get(path));
    }

    public Object get() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        return m_values.get(null);
    }

    public boolean next() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

	if (m_position == -1) {
	    return false;
	}

        if (m_rs == null) {
            getSession().flush();
            m_rs = execute();
        }

        if (m_rs.next()) {
            m_values = m_rs.load(getSession());

            m_position++;
            return true;
        } else {
            m_position = -1;
            free();
            return false;
        }
    }

    protected RecordSet execute() {
	return getSession().getEngine().execute(m_ds.getSignature(),
                                                m_ds.getExpression());
    }

    public boolean isBeforeFirst() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        return m_position == 0;
    }

    public boolean isFirst() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        return m_position == 1;
    }

    public boolean isAfterLast() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        return m_position == -1;
    }

    public long getPosition() {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        if (m_position > 0) {
            return m_position;
        } else {
            return 0;
        }
    }

    public void rewind() {
        close();
        m_position = 0;
	m_closed = false;
    }

    private void free() {
        if (m_rs != null) {
            m_rs.close();
	    m_rs = null;
        }
    }

    public void close() {
	free();
	m_closed = true;
    }

}
