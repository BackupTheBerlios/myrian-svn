/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
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
 * @version $Revision: #3 $ $Date: 2002/08/13 $
 */

public class MetaTest extends PersistenceTestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/MetaTest.java#3 $ by $Author: dennis $, $DateTime: 2002/08/13 11:53:00 $";
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
        load("com/arsdigita/persistence/testpdl/static/Datatype.pdl");
        load("com/arsdigita/persistence/testpdl/static/Link.pdl");
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/static/Order.pdl");
        load("com/arsdigita/persistence/testpdl/static/Party.pdl");
        super.persistenceSetUp();
    }

    public void testGenericCRUD()  throws Exception {
        ObjectTypeValidator validator = new ObjectTypeValidator(getSession());
        validator.performCRUDTest(m_objectTypeName); 

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
        suite.addTest(new MetaTest("examples.Article"));
//        suite.addTest(new MetaTest("examples.ArticleImageLink"));
       suite.addTest(new MetaTest("examples.Image"));
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
