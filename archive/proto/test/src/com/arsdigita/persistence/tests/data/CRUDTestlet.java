package com.arsdigita.persistence.tests.data;


import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.util.*;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * CRUDTestlet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class CRUDTestlet extends Testlet {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/tests/data/CRUDTestlet.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private static final Logger LOG = Logger.getLogger(CRUDTestlet.class);

    private ObjectType m_type;

    private DataSource m_initial = new DataSource("initial values");
    private DataSource m_updated = new DataSource("updated values");

    public CRUDTestlet(String type) {
        this(MetadataRoot.getMetadataRoot().getObjectType(type));
    }

    public CRUDTestlet(ObjectType type) {
        m_type = type;
    }

    public void run() {
        ObjectTree tree =
            makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION, 1);
        ObjectTree updated =
            makeTree(m_type, ATTRIBUTE | ROLE, COLLECTION | KEY, 1);
        LOG.warn("Tree: " + tree);
        LOG.warn("Updated: " + updated);

        Session ssn = SessionManager.getSession();

        // Create
        DataObject data = create(tree, m_initial);
        OID oid = data.getOID();
        data.save();

        // Retrieve and verify
        verify(ssn.retrieve(oid), tree, m_initial);

        // Update
        data = ssn.retrieve(oid);
        update(data, updated, m_updated);
        data.save();

        // Retrieve and verify
        verify(ssn.retrieve(oid), updated, m_updated);

        // Delete
        // XXX: This doesn't delete the entire tree.
        data = ssn.retrieve(oid);
        data.delete();

        // Verify that it is deleted
        Assert.assertEquals(null, ssn.retrieve(oid));
    }

}
