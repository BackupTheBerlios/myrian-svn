package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #13 $ $Date: 2003/02/28 $
 **/

public class Cursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Cursor.java#13 $ by $Author: rhs $, $DateTime: 2003/02/28 19:58:14 $";

    final private Session m_ssn;
    final private Query m_query;
    final private Signature m_signature;

    private RecordSet m_rs = null;
    private Object m_current = null;
    private long m_position = 0;

    protected Cursor(Session ssn, Query query) {
        m_ssn = ssn;
        m_query = query;
        m_signature = query.getSignature();
    }

    public Session getSession() {
        return m_ssn;
    }

    public Object get(Path path) {
        if (m_signature.getPath(path.getPath()) == null) {
            throw new IllegalArgumentException
                ("Path is not in Cursor signature: " + path);
        }

        if (m_current == null) {
            throw new IllegalStateException
                ("Cursor not currently on row.");
        }
        return m_ssn.get(m_current, path);
    }

    public Object get(String path) {
        Path p = m_signature.getPath(path);

        if (p == null) {
            throw new IllegalArgumentException
                ("Path is not in Cursor signature: " + path);
        }

        return get(p);
    }

    public Object get() {
        return m_current;
    }

    public boolean next() {
        if (m_rs == null) {
            m_ssn.flush();
            m_rs = m_ssn.getEngine().execute(m_query);
        }

        if (m_rs.next()) {
            m_current = m_rs.load(m_ssn);

            m_position++;
            return true;
        } else {
            m_position = 0;
            return false;
        }
    }

    public long getPosition() {
        return m_position;
    }

    public void close() {
        if (m_rs != null) {
            m_rs.close();
        }
    }

}
