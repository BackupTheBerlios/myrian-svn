package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #17 $ $Date: 2003/03/27 $
 **/

public class Cursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Cursor.java#17 $ by $Author: rhs $, $DateTime: 2003/03/27 15:13:02 $";

    final private Session m_ssn;
    final private Query m_query;
    final private Signature m_signature;

    private RecordSet m_rs = null;
    private Map m_values = null;
    private long m_position = 0;

    protected Cursor(Session ssn, Query query) {
        m_ssn = ssn;
        m_query = query;
        m_signature = query.getSignature();
    }

    public Session getSession() {
        return m_ssn;
    }

    private boolean check(Path path) {
	for (Iterator it = m_signature.getPaths().iterator(); it.hasNext(); ) {
	    Path p = (Path) it.next();
	    if (path.isAncestor(p)) {
		return true;
	    }
	}

	return false;
    }

    private Object getInternal(Path path) {
	if (m_signature.isSource(path)) {
	    return m_values.get(path);
	} else {
	    Object o = getInternal(path.getParent());
	    return m_ssn.get(o, Path.get(path.getName()));
	}
    }

    public Object get(Path path) {
        if (!check(path)) {
            throw new IllegalArgumentException
                ("Path is not in Cursor signature: " + path);
        }

        if (m_position <= 0) {
            throw new IllegalStateException
                ("Cursor not currently on row.");
        }

        return getInternal(path);
    }

    public Object get(String path) {
        return get(Path.get(path));
    }

    public Object get() {
        return m_values.get(null);
    }

    public boolean next() {
        if (m_rs == null) {
            m_ssn.flush();
            m_rs = m_ssn.getEngine().execute(m_query);
        }

        if (m_rs.next()) {
            m_values = m_rs.load(m_ssn);

            m_position++;
            return true;
        } else {
            m_position = -1;
            close();
            return false;
        }
    }

    public boolean isBeforeFirst() {
        return m_position == 0;
    }

    public boolean isFirst() {
        return m_position == 1;
    }

    public boolean isAfterLast() {
        return m_position == -1;
    }

    public long getPosition() {
        if (m_position > 0) {
            return m_position;
        } else {
            return 0;
        }
    }

    public void rewind() {
        m_rs.close();
        m_rs = null;
        m_position = 0;
    }

    public void close() {
        if (m_rs != null) {
            m_rs.close();
        }
    }

}
