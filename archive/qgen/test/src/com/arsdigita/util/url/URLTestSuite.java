package com.arsdigita.util.url;

import com.arsdigita.tools.junit.framework.PackageTestSuite;
import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import junit.framework.Test;
import org.apache.log4j.BasicConfigurator;

/*
 * Copyright (C) 2003, 2003, 2003 Red Hat Inc. All Rights Reserved.
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


/**
 * URLTestSuite
 *
 */
public class URLTestSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/util/url/URLTestSuite.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    public URLTestSuite() {
        super();
    }

    public URLTestSuite(Class theClass) {
        super(theClass);
    }

    public URLTestSuite(String name) {
        super(name);
    }

    public static Test suite() {
        URLTestSuite suite = new URLTestSuite();
        populateSuite(suite);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
//        wrapper.setInitScriptTarget ("com.arsdigita.logging.Initializer");
        return wrapper;
    }

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run( suite() );

    }

}
