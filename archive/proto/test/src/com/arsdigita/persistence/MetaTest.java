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

package com.arsdigita.persistence;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import junit.framework.*;
import com.arsdigita.persistence.metadata.*;
import java.math.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;

/**
 * MetaTest
 *
 * @author <a href="mailto:jorris@arsdigita.com"Jon Orris</a>
 * @version $Revision: #5 $ $Date: 2003/08/04 $
 */

public class MetaTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/proto/test/src/com/arsdigita/persistence/MetaTest.java#5 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";
    private static final Logger s_log =
        Logger.getLogger(MetaTest.class.getName());
    static  {
        s_log.setPriority(Priority.DEBUG);
    }

    String m_objectTypeName;
    public MetaTest(String objectTypeName) {
        super("testGenericCRUD");
        m_objectTypeName = objectTypeName;
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Party.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/Order.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperation.pdl");
        load("com/arsdigita/persistence/testpdl/static/DataOperationExtra.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        load("com/arsdigita/persistence/testpdl/static/Link.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        super.persistenceSetUp();
    }

    public void testGenericCRUD()  throws Exception {
        try {
            ObjectTypeValidator validator =
                new ObjectTypeValidator(getSession());
            validator.performCRUDTest(m_objectTypeName);
        } catch (AbortMetaTestException e) {
            // This happens on postgres when the validator encounters a valid
            // error that on postgres destroys the transaction and keeps the
            // validator from continuing its testing. Eventually we should
            // have a better way of dealing with this.
        }
    }
    public String getName() {
        return super.getName() + ":" + m_objectTypeName;
    }
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new MetaTest("mdsql.Order"));
        suite.addTest(new MetaTest("mdsql.LineItem"));
        suite.addTest(new MetaTest("mdsql.Party"));
        suite.addTest(new MetaTest("mdsql.User"));
        suite.addTest(new MetaTest("mdsql.Group"));
        suite.addTest(new MetaTest("linkTest.Article"));
        //        suite.addTest(new MetaTest("linkTest.ArticleImageLink"));
        suite.addTest(new MetaTest("linkTest.Image"));
        suite.addTest(new MetaTest("examples.Node"));
        suite.addTest(new MetaTest("examples.Order"));
        //        suite.addTest(new MetaTest("examples.LineItem"));
        suite.addTest(new MetaTest("examples.Party"));


        suite.addTest(new MetaTest("examples.User"));
        suite.addTest(new MetaTest("examples.Group"));
        suite.addTest(new MetaTest("examples.Datatype"));
        return suite;
    }

}
