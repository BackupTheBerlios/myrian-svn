package com.redhat.persistence;

import com.redhat.persistence.common.*;

import java.util.*;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2003/08/04 $
 **/

public class Cursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/Cursor.java#1 $ by $Author: dennis $, $DateTime: 2003/08/04 15:56:00 $";

    final private Session m_ssn;
    final private Query m_query;
    final private Signature m_signature;

    private RecordSet m_rs = null;
    private Map m_values = null;
    private long m_position = 0;
    private boolean m_closed = false;

    protected Cursor(Session ssn, Query query) {
        m_ssn = ssn;
        m_query = query;
        m_signature = query.getSignature();
    }

    public Session getSession() {
        return m_ssn;
    }

    public boolean isClosed() {
	return m_closed;
    }

    private Object getInternal(Path path) {
	if (m_signature.isSource(path)) {
	    return m_values.get(path);
	} else {
	    Object o = getInternal(path.getParent());
            if (o == null) { return null; }
	    return m_ssn.get(o, Path.get(path.getName()));
	}
    }

    public Object get(Path path) {
	if (m_closed) {
	    throw new ClosedException(this);
	}

        if (!m_signature.isFetched(path)) {
            throw new NotFetchedException(this, path);
        }

        if (m_position <= 0) {
	    throw new NoRowException(this);
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
            m_ssn.flush();
            m_rs = execute();
        }

        if (m_rs.next()) {
            m_values = m_rs.load(m_ssn);

            m_position++;
            return true;
        } else {
            m_position = -1;
            free();
            return false;
        }
    }

    protected RecordSet execute() {
	return m_ssn.getEngine().execute(m_query);
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
