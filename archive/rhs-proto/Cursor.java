package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.persistence.OID;

import java.util.*;

/**
 * Cursor
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/11/27 $
 **/

public abstract class Cursor {

    public final static String versionId = "$Id: //users/rhs/persistence-proto/Cursor.java#2 $ by $Author: rhs $, $DateTime: 2002/11/27 17:41:53 $";

    private Session m_ssn;
    private Signature m_signature;
    private OID m_current = null;

    protected Cursor(Session ssn, Signature signature) {
        m_ssn = ssn;
        m_signature = signature;
    }

    public Session getSession() {
        return m_ssn;
    }

    public Signature getSignature() {
        return m_signature;
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
        if (fetchRow()) {
            OID m_current = load();
            return true;
        } else {
            m_current = null;
            return false;
        }
    }

    private OID load() {
        Collection paths = m_signature.getPaths();
        ObjectType type = m_signature.getObjectType();

        HashMap oids = new HashMap();

        // First load up the OIDs.
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path parent = p.getParent();
            if (p.isKey(type)) {
                OID oid;
                if (oids.containsKey(parent)) {
                    oid = (OID) oids.get(parent);
                } else {
                    if (parent == null) {
                        oid = new OID(type);
                    } else {
                        oid = new OID((ObjectType) parent.getType(type));
                    }
                    oids.put(parent, oid);
                }

                oid.set(p.getName(), fetchPath(p));
            }
        }

        // Now load up the values.
        for (Iterator it = paths.iterator(); it.hasNext(); ) {
            Path p = (Path) it.next();
            Path parent = p.getParent();

            OID oid = (OID) oids.get(parent);
            if (!oid.isInitialized()) {
                throw new IllegalStateException
                    ("Query does not fetch OID values.");
            }

            Property prop = oid.getObjectType().getProperty(p.getName());

            if (p.isKey(type)) {
                m_ssn.load(oid, prop, oid.get(p.getName()));
            } else {
                m_ssn.load(oid, prop, fetchPath(p));
            }
        }

        return (OID) oids.get(null);
    }

    protected abstract boolean fetchRow();

    protected abstract Object fetchPath(Path p);

}
