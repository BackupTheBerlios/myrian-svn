package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import org.apache.log4j.Category;

/**
 * QueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

public class QueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/QueryTest.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static final Category s_log =
        Category.getInstance(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/oql/pdl/QueryTest.pdl");
    }

    protected void persistenceTearDown() {
        // Do nothing
    }

    public void test() {
        MetadataRoot root = MetadataRoot.getMetadataRoot();

        Query q = new Query(root.getObjectType("test.QueryTest"));
        //q.fetchDefault();
        q.fetch("parent");
        q.generate();
        System.out.println(q.toSQL());
    }

}
