/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class GenericDataQueryTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/GenericDataQueryTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
