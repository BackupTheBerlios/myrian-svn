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
package com.arsdigita.tools.junit.extensions;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.TimedTestRecord;
import com.arsdigita.tools.junit.framework.PackageTestSuite;

import junit.framework.*;
import junit.extensions.TestSetup;
import com.clarkware.junitperf.*;

import com.arsdigita.categorization.*;

/**
 * <P>Example of {@link TimedTestRecord} usage </P> 
 *
 * @author <a href="mailto:aahmed@redhat.com">Aizaz Ahmed</a>
 */
public class RecordTestSuite extends PackageTestSuite {


    public static Test suite() {
        RecordTestSuite suite = new RecordTestSuite ();

        Test CategoryTest = new CategoryTest ( "testIsEnabled" );
        Test CategoryTest2 = new CategoryTest ( "testSetGetProperties" );
        Test timedRecordedTest = new TimedTestRecord ( (TestCase) CategoryTest );
        suite.addTest ( timedRecordedTest );
        Test timedRecordedTest2 = new TimedTestRecord ( (TestCase) CategoryTest2 );
        suite.addTest ( timedRecordedTest2 );
        
        BaseTestSetup wrapper = new BaseTestSetup(suite);
        
        wrapper.setInitScriptTarget ("com.arsdigita.kernel.security.Initializer");
        return wrapper;
    }
}
