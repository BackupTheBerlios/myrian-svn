/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
 * @author Michael Bryzek
 * @date $Date: 2004/08/30 $
 * @version $Revision: #2 $
 *
 * @see com.arsdigita.persistence.Initializer
 **/

public class InitializerTest extends TestCase
{

    public static final String versionId = "$Id: //eng/persistence/dev/cap/test/src/com/arsdigita/persistence/InitializerTest.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
