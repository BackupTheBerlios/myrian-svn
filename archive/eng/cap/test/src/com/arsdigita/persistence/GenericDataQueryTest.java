/*
 * Copyright (C) 2002-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
 * @version $Revision: #3 $ $Date: 2004/09/01 $
 **/

public class GenericDataQueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/GenericDataQueryTest.java#3 $ by $Author: dennis $, $DateTime: 2004/09/01 11:40:07 $";

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
