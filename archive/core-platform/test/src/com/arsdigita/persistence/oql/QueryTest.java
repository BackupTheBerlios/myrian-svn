package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import org.apache.log4j.Category;

/**
 * QueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/05/21 $
 **/

public class QueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/QueryTest.java#2 $ by $Author: rhs $, $DateTime: 2002/05/21 20:57:49 $";

    private static final Category s_log =
        Category.getInstance(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/oql/pdl/QueryTest.pdl");
        load("com/arsdigita/persistence/oql/pdl/ResourceImpl.pdl");
        load("com/arsdigita/persistence/oql/pdl/File.pdl");
        load("com/arsdigita/persistence/oql/pdl/Profile.pdl");
    }

    protected void persistenceTearDown() {
        // Do nothing
    }

    // This isn't a real test suite yet because there isn't really a way to
    // verify whether these pass or fail short of inspecting them manually,
    // but these are good cases to test so I'm keeping track of them here
    // anyways. Maybe if someone gets motivated at some point we can set up
    // regressions against the sql produced.


    /**
     * Tests fetching a parent property that is a self reference. This used to
     * result in an unconstrained join.
     **/

    public void testSelfReference() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();

        Query q = new Query(root.getObjectType("test.QueryTest"));
        //q.fetchDefault();
        q.fetch("parent");
        q.generate();
    }


    /**
     * Tests aggressively loading two optional properties. This used to result
     * in outer joining the same table twice, thereby producing invalid sql.
     **/

    public void testTwoOptionalAggressiveLoads() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        Query q = new Query(root.getObjectType("test.Profile"));
        q.fetchDefault();
        q.generate();
        System.out.println(q.toDot());
    }

}
