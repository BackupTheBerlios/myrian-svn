package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.metadata.*;
import com.arsdigita.persistence.metadata.Table;
import com.arsdigita.db.Initializer;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * QueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2002/08/13 $
 **/

public class QueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/QueryTest.java#8 $ by $Author: dennis $, $DateTime: 2002/08/13 11:53:00 $";

    private static final Logger s_log =
        Logger.getLogger(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }

    private void doTest(String name, String typeName, String[] properties) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        ObjectType type = root.getObjectType(typeName);
        assertTrue("No such type: " + typeName, type != null);
        Query query = new Query(type);

        if (properties == null) {
            query.fetchDefault();
        } else {
            for (int i = 0; i < properties.length; i++) {
                query.fetch(properties[i]);
            }
        }

        query.generate();
        Operation actual = query.getOperation();
        compare(name + ".op", actual.toString());
    }

    private void compare(String expectedResource, String actual) {
        String op = "com/arsdigita/persistence/oql/" + expectedResource;
        InputStream is = getClass().getClassLoader().getResourceAsStream(op);

        if (is == null) {
            // this means it is a db specific file
            String database = null;
            if (Initializer.getDatabase() == Initializer.POSTGRES) {
                database = "postgres";
            } else {
                database = "oracle-se";
            }
            op = "com/arsdigita/persistence/oql/" + database + "/" + 
                expectedResource;
            is = getClass().getClassLoader().getResourceAsStream(op);
        }
        assertTrue("No such resource: " + op + "\n\nActual:\n" + actual,
                   is != null);

        Reader reader = new InputStreamReader(is);
        StringBuffer expected = new StringBuffer();
        char[] buf = new char[1024];
        try {
            while (true) {
                int n = reader.read(buf);
                if (n < 0) { break; }
                expected.append(buf, 0, n);
            }
        } catch (IOException e) {
            fail(e.getMessage());
        }

        doDiff(expected.toString(), actual);
    }

    private void doDiff(String expected, String actual) {
        if (stripWhitespace(expected).equals(stripWhitespace(actual))) {
            return;
        } else {
            fail(expected, actual);
        }
    }

    private static final String stripWhitespace(String str) {
        StringBuffer result = new StringBuffer();

        str = StringUtils.stripWhiteSpace(str);
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isWhitespace(c)) {
                char last;
                if (result.length() > 0) {
                    last = result.charAt(result.length() - 1);
                } else {
                    last = '\0';
                }
                if (last != ' ') {
                    result.append(' ');
                }
            } else {
                result.append(c);
            }
        }

        return result.toString().trim();
    }

    private static final void fail(String expected, String actual) {
        fail("Expected: \n" + expected + "\n\nActual:\n " + actual);
    }

    /**
     * Tests fetching a parent property that is a self reference. This used to
     * result in an unconstrained join.
     **/

    public void testSelfReference() {
        doTest("SelfReference",
               "oql.SelfReference",
               new String[] {
                   "parent"
               });
    }


    /**
     * Tests aggressively loading two optional properties. This used to result
     * in outer joining the same table twice, thereby producing invalid sql.
     **/

    public void testTwoOptionalAggressiveLoads() {
        doTest("TwoOptionalAggressiveLoads",
               "oql.TwoOptionalAggressiveLoads",
               null);
    }

    /**
     * Tests whether the optimizer retains the subtype table when only
     * fetching attributes from the supertype.
     **/

    public void testSubtypeTableRetention() {
        doTest("SubtypeTableRetention",
               "oql.Sub",
               new String[] {
                   "id",
                   "supAttribute"
               });
    }

    /**
     * Tests whether the optimizer will be smart enough to eliminate an
     * extraneous outer join.
     **/

    public void testEliminateOuterJoin() {
        doTest("EliminateOuterJoin",
               "oql.Sub",
               new String[] {
                   "id",
                   "optional.id"
               });
    }

    /**
     * Tests whether the optimizer will be smart enough to eliminate an
     * extraneous inner join.
     **/

    public void testEliminateInnerJoin() {
        doTest("EliminateInnerJoin",
               "oql.Sub",
               new String[] {
                   "id",
                   "required.id"
               });
    }

    /**
     * Tests fetching a required property.
     **/

    public void testRequiredFetch() {
        doTest("RequiredFetch",
               "oql.Sub",
               new String[] {
                   "id",
                   "required"
               });
    }

    /**
     * Tests fetching an optional property.
     **/

    public void testOptionalFetch() {
        doTest("OptionalFetch",
               "oql.Sub",
               new String[] {
                   "id",
                   "optional"
               });
    }

    private void doTableTest(String tableName) {
        MetadataRoot root = MetadataRoot.getMetadataRoot();
        Table table = root.getTable(tableName);
        assertTrue("No such table: " + tableName, table != null);
        compare(table.getName() + ".sql", table.getSQL(false));
    }

    public void testTest() {
        doTableTest("tests");
    }

    public void testIcles() {
        doTableTest("icles");
    }

    public void testComponents() {
        doTableTest("components");
    }

    public void testCollectionSelf() {
        doTableTest("collection_self");
    }

    public void testCollection() {
        doTableTest("collection");
    }

}
