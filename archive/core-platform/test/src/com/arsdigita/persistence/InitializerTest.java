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

import com.arsdigita.persistence.metadata.MetadataRoot;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p> This class ensures that we can load the persistence metadata
 * XML file at initialization
 *
 * </p>
 *
 *
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @date $Date: 2002/10/16 $
 * @version $Revision: #4 $
 *
 * @see com.arsdigita.persistence.Initializer
 **/

public class InitializerTest extends TestCase
{

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/InitializerTest.java#4 $ by $Author: dennis $, $DateTime: 2002/10/16 14:12:35 $";

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     **/
    public InitializerTest(String name) {
        super(name);
    }

    /**
     * Test method: void addFile(String)
     **/
    public void testStartupXML() {
        MetadataRoot root = SessionManager.getMetadataRoot();
        if (root == null) {
            fail("Metadata root not loaded");
        }
        // Make sure we have at least one schema or model
        if (!root.getModels().hasNext()) {
            fail("Metadata root has no schema or model. Check that you have " +
                 "correctly specified the file paths in the xmlFiles parameter " +
                 "in the init script you are using.");
        }
    }


    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     **/
    public static void main(String[] args) {
        junit.textui.TestRunner.run( new TestSuite(InitializerTest.class) );
    }
}
