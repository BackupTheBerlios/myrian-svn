/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the GNU Public License
 * Version 2 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/doc/license.text
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.tools.junit.extensions;

import com.arsdigita.initializer.Startup;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.util.ResourceManager;

import junit.extensions.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 *  Initializer
 *  
 * @author <a href="mailto:dennis@arsdigita.com">Dennis Gregorovic</a>
 * @version $Revision: #1 $ $Date: 2002/05/15 $
 */

public class Initializer {

    private static Startup s_startup;
    private static String  s_webAppRoot;


    /**                                                                 
     *  This method reads a given test init script, and runs all of the Initializers
     *  defined in it upto and including iniName
     * 
     *  @param suite  Any errors are added as failing tests to the TestSuite. 
     *  @param scriptName Name of the initializer script. If null, the system property 
     *          test.initscript will be used.
     *  @iniName The name of the last Initializer in scriptName to be run.
     */
    public static void startup(TestSuite suite, 
                               String scriptName,
                               String iniName) {
        if (scriptName == null) {
            scriptName = System.getProperty("test.initscript");
            if (scriptName == null) {
                reportWarning (suite, "Property test.initscript not set. This " +
                    "property is defined in your ant.properties file. It " + 
                    "should be set to the full path of a valid init script " +
                    "(e.g. enterprise.init)");
                return;
            }
        }
        s_webAppRoot = System.getProperty("test.webapp.dir");

        System.out.println ("starting initializers " + scriptName + 
                            " ; iniName: " + iniName);

        try {
            s_startup = new Startup(s_webAppRoot, scriptName);
            s_startup.setLastInitializer (iniName);
            s_startup.init();
        } catch (InitializationException e) {
            reportWarning (suite, "Initialization failed with message: " + 
                           e.getMessage());
        }

    }

    protected static void startup(TestSuite suite, String scriptName) {
        startup (suite, scriptName, null);
    }
    
    protected static void startup(TestSuite suite) {
        startup (suite, null);
    }

    protected static void shutdown() {
        System.out.println ("stopping initializers");
        
        if (s_startup != null) {
            try {
                s_startup.destroy();
            } catch (InitializationException e) { }
        }
    }

    private static void reportWarning (TestSuite suite, final String message) {
        System.err.println (message);
        suite.addTest( warning (message));
    }
        
    private static Test warning(final String message) {
		return new TestCase("warning") {
			protected void runTest() {
				fail(message);
			}
		};		
	}
}
