package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2003/01/06 $
 **/

public class Cursor {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/Cursor.java#6 $ by $Author: rhs $, $DateTime: 2003/01/06 17:58:56 $";

    private DataSet m_dset;
    private Session m_ssn;
    private Signature m_signature;

    private RecordSet m_rs = null;
    private OID m_current = null;

    protected Cursor(DataSet dset) {
        m_dset = dset;
        m_ssn = dset.getSession();
        m_signature = dset.getQuery().getSignature();
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
        return path.get(m_ssn, m_current);
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
        return m_ssn.retrieve(m_current);
    }

    public boolean next() {
        if (m_rs == null) {
            m_ssn.flush();
            m_rs = m_ssn.getEngine().execute(m_dset.getQuery());
        }

        if (m_rs.next()) {
            m_current = m_rs.load(m_ssn);

            return true;
        } else {

            return false;
        }
    }

}
