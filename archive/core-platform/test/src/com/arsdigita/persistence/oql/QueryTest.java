/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence.oql;

import com.arsdigita.persistence.*;
import com.arsdigita.persistence.proto.*;
import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;
import com.arsdigita.persistence.proto.engine.rdbms.*;
import com.arsdigita.db.DbHelper;
import com.arsdigita.util.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * QueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #14 $ $Date: 2003/05/12 $
 **/

public class QueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/oql/QueryTest.java#14 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final Logger s_log =
        Logger.getLogger(QueryTest.class);

    public QueryTest(String name) {
        super(name);
    }

    private void doTest(String name, String typeName, String[] properties) {
        Root root = Root.getRoot();
        ObjectType type = root.getObjectType(typeName);
        assertTrue("No such type: " + typeName, type != null);
        Signature sig = new Signature(type);
        Query query = new Query(sig, null);

        if (properties == null) {
            sig.addDefaultProperties();
        } else {
            for (int i = 0; i < properties.length; i++) {
                sig.addPath(Path.get(properties[i]));
            }
        }

        OracleWriter w = new OracleWriter();
        w.write(query);
        compare(name + ".op", w.getSQL());
    }

    private void compare(String expectedResource, String actual) {
        String op = "com/arsdigita/persistence/oql/" + expectedResource;
        InputStream is = getClass().getClassLoader().getResourceAsStream(op);

        if (is == null) {
            // this means it is a db specific file
            String database = null;
            if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
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
        StringTokenizer expectedTokens = new StringTokenizer(expected, "\n\r");
        StringTokenizer actualTokens = new StringTokenizer(actual, "\n\r");

        int lineNumber = 0;
        while (expectedTokens.hasMoreTokens()) {
	    lineNumber++;
	    String expectedLine = stripWhitespace(expectedTokens.nextToken());
            if (actualTokens.hasMoreTokens()) {
                String actualLine = stripWhitespace(actualTokens.nextToken());
                if(!expectedLine.equals(actualLine)) {
                    fail(expectedLine, actualLine, lineNumber, actual);
                }
            } else {
                fail(expectedLine, null, lineNumber, actual);
            }

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

    private static final void fail(String expected, String actual,
				   int lineNumber, String output) {
        fail("Diff failed at line " + lineNumber +
	     "\nExpected line:\n" + expected + "\n\nActual line:\n" + actual +
	     "\n\nTest output:\n" + output);
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
        Root root = Root.getRoot();
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
