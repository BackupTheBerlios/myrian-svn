/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.*;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.*;
import org.apache.log4j.Logger;

/**
 * GenericDataQueryTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/04/07 $
 **/

public class GenericDataQueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/GenericDataQueryTest.java#10 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private static final Logger s_log =
        Logger.getLogger(GenericDataQueryTest.class);

    public GenericDataQueryTest(String name) {
        super(name);
    }

    private static String SELECT = null;

    private DataQuery makeTestQuery() {
        if (SELECT == null) {
            if (DbHelper.getDatabase
                (getSession().getConnection()) == DbHelper.DB_POSTGRES) {
                SELECT = "select 1 as one ";
            } else {
                SELECT = "select 1 as one from dual";
            }
        }
        Session ssn = SessionManager.getSession();
        DataQuery result =
            new GenericDataQuery(ssn, SELECT, new String[] {"one"});
        return result;
    }

    private void addTestFilter(DataQuery dq) {
        dq.setRange(new Integer(1), new Integer(10));
        dq.setParameter("foo", "bar");
        Filter f = dq.addFilter("1 > :value");
        f.set("value", new Integer(2));
        dq.addEqualsFilter("one", new Integer(2));
    }

    private void assertResult(DataQuery dq) {
        assertTrue("Filter didn't take effect.", !dq.next());
    }

    public void test1() {
        DataQuery original = makeTestQuery();
        addTestFilter(original);
        DataQuery dq = new DataQueryDataCollectionAdapter(original, "");
        assertResult(dq);
    }

    public void test2() {
        DataQuery original = makeTestQuery();
        DataQuery dq = new DataQueryDataCollectionAdapter(original, "");
        addTestFilter(dq);
        assertResult(dq);
    }

    public void test3() {
        DataQuery dq = makeTestQuery();
        addTestFilter(dq);
        assertResult(dq);
    }

}
