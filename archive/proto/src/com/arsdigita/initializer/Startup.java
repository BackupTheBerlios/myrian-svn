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

package com.arsdigita.initializer;

import com.arsdigita.util.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;

/**
 * Convenience class designed to run initializers. Either manually specify the
 * values for web app root and script name, or set java properties with names
 * WEB_APP_ROOT or SCRIPT_NAME.
 *
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @author <a href="dennis@arsdigita.com">Dennis Gregorovic</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 * @since ACS 4.7
 *
 **/
public class Startup {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/initializer/Startup.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    /** The name of the property containing the web app root **/
    public static final String WEB_APP_ROOT = "webAppRoot";

    /** The name of the property containing the script name **/
    public static final String SCRIPT_NAME = "scriptName";

    private String m_webAppRoot;
    private String m_scriptName;
    private String m_lastInitializer;
    private Script m_ini;

    /**
     * Sets up environment variables. Example:
     *
     *<pre>
     * String scriptName = "/WEB-INF/resources/enterprise.init";
     * String webAppRoot =
     *    "/usr/local/jakarta-tomcat-3.2.3/webapps/enterprise";
     *
     *  Startup startup = new Startup(webAppRoot, scriptName);
     *  startup.init();
     *</pre>
     *
     * @param webAppRoot The web app root to use (e.g. $TOMCAT_HOME/webapps/enterprise);
     * @param scriptName The relative (from web app root) path to the
     * script that defines the initializers
     * (e.g. /WEB-INF/resources/enterprise.init)
     *
     **/
    public Startup(String webAppRoot, String scriptName) {
        m_webAppRoot = webAppRoot;
        m_scriptName = scriptName;
    }


    /**
     * Wrapper for {@link #Startup(String, String)} which looks for
     * the system properties named WEB_APP_ROOT and SCRIPT_NAME.
     *
     * @exception InitializationException If we cannot find either property.
     **/
    public Startup() throws InitializationException {
        this(getProperty(WEB_APP_ROOT), getProperty(SCRIPT_NAME));
    }


    /**
     * Sets the name of the last initializer to run. If not set, all the
     * initializers will run.
     *
     * @param lastInitializer The name of the last initializer to run
     * (e.g. com.arsdigita.persistence.Initializer)
     **/
    public void setLastInitializer(String lastInitializer) {
        m_lastInitializer = lastInitializer;
    }


    /**
     * Starts up the web environment for the ACS.
     *
     * @return Collection of the names of all initializers run.
     **/
    public Collection init() throws InitializationException {
        ResourceManager rm = ResourceManager.getInstance();
        rm.setWebappRoot(new File(m_webAppRoot));

        Reader r;
        try {
            r = new FileReader(m_scriptName);
        } catch (FileNotFoundException e) {
            throw new InitializationException("Couldn't find " + m_scriptName);
        }

        Collection initializersRun = null;
        try {
            if (m_lastInitializer == null) {
                m_ini = new Script(r);
                initializersRun = m_ini.startup();
            } else {
                m_ini = new Script(r, m_lastInitializer);
                initializersRun = m_ini.startup(m_lastInitializer);
            }
        } catch (InitializationException e) {
            e.printStackTrace(System.err);
            throw new InitializationException
                ("Error loading init script: " + e.getMessage());
        }

        return initializersRun;
    }


    /**
     * Shut down the startup script.
     **/
    public void destroy() {
        m_ini.shutdown();
    }

    /**
     * Helper method to retrieve the specified property or throw an
     * exception if the property doesn't exist or if the property was
     * the empty string.
     **/
    private static String getProperty(String propertyName) throws InitializationException {
        String property = System.getProperty(propertyName);
        if (property == null || property.trim().length() == 0) {
            throw new InitializationException
                ("The " + propertyName + " system property could not be " +
                 "found or was empty");
        }
        return property;
    }
}
