package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Property;

import java.util.*;

/**
 * RecordSet
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/02 $
 **/

public abstract class RecordSet {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/RecordSet.java#2 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    private Signature m_signature;

    protected RecordSet(Signature signature) {
        m_signature = signature;
    }

    public Signature getSignature() {
        return m_signature;
    }

    public abstract boolean next();

    public abstract Object get(Path p);

    OID load(Session ssn) {
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

                oid.set(p.getName(), get(p));
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
                ssn.load(oid, prop, oid.get(p.getName()));
            } else {
                ssn.load(oid, prop, get(p));
            }
        }

        return (OID) oids.get(null);
    }

}
