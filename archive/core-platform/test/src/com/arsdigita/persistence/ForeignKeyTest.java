package com.arsdigita.persistence;

import com.arsdigita.util.Assert;

public class ForeignKeyTest extends PersistenceTestCase {

    public ForeignKeyTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/ForeignKey.pdl"); 
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/static/ForeignKey.pdl"); 
        super.persistenceTearDown();
    }

    /**
     * Insure that indices exist for all foreign keys.
     */
    public void testForeignKeyIndicesExist() {
        DataQuery dq = getSession().retrieveQuery("examples.ForeignKeyIndices");
        assertEquals("Missing index(es) for foreign keys", 0, dq.size());
    }

}
