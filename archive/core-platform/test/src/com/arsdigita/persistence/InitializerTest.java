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

import com.arsdigita.persistence.metadata.MetadataRoot;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * <p> This class ensures that we can load the persistence metadata
 * XML file at initialization
 *
 * </p>
 * 
 * <p> Copyright 2001 ArsDigita Corporation</p>
 * 
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @date $Date: 2002/07/18 $
 * @version $Revision: #2 $
 * 
 * @see com.arsdigita.persistence.Initializer
 **/

public class InitializerTest extends TestCase
{

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/InitializerTest.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

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
