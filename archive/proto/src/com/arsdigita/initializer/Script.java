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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Script
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/08/04 $
 */

public class Script {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/initializer/Script.java#5 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    private static final Logger s_log =
        Logger.getLogger(Script.class);

    private List m_initializers = new ArrayList();
    private String m_lastInitializerToRun;
    private boolean m_isStarted = false;
    private boolean m_isShutdown = false;

    /**
     * Constructs a new initialization script from the given string buffer.
     *
     * @param bs The script.
     **/

    public Script(StringBuffer bs) throws InitializationException {
        this(bs, null);
    }

    public Script(StringBuffer bs, String iniName) throws InitializationException {
        this(new StringReader(bs.toString()), iniName);
    }

    /**
     * Constructs a new initialization script from the given string.
     *
     * @param s The script.
     **/

    public Script(String s) throws InitializationException {
        this(s, null);
    }

    public Script(String s, String iniName) throws InitializationException {
        this(new StringReader(s), iniName);
    }

    /**
     * Constructs a new initialization script from the given input stream.
     *
     * @param is The script.
     **/

    public Script(InputStream is) throws InitializationException {
        this(is, null);
    }

    public Script(InputStream is, String iniName) throws InitializationException {
        this(new InputStreamReader(is), iniName);
    }

    /**
     * Constructs a new initialization script from the given reader.
     *
     * @param r The script.
     **/

    public Script(Reader r) throws InitializationException {
        this(r, null);
    }

    /**
     * Constructs a new Script
     *
     * @param r Reader for the script parser
     * @param iniName Name of the last initializer to run, or null. 
     * Used to selectively run only part of the initialization script
     *
     * @throws InitializationException
     */
    public Script(Reader r, String iniName) throws InitializationException {
        m_lastInitializerToRun = iniName;
        ScriptParser sp = new ScriptParser(r);
        try {
            sp.parse(this);
        } catch (ParseException e) {
            // FIXME: what's the purpose of the errTok variable? I'm commenting
            // it out. -- 2002-11-26

            // Token errTok = e.currentToken.next;
            throw new InitializationException(e.getMessage());
        }
    }

    /**
     * Adds an initializer to the script.
     *
     * @param ini The initializer.
     * @return true if the parser should continue adding initializers
     **/
    public boolean addInitializer(Initializer ini)
        throws InitializationException {
        if (m_isStarted) {
            throw new InitializationException(
                "This script has already been started."
            );
        }
        final String initializerName = ini.getClass().getName();
        m_initializers.add(ini);

        final boolean continueAddingInitializers = 
            !initializerName.equals(m_lastInitializerToRun);
        return continueAddingInitializers;
    }

    /**
     * Returns all the initializers specified in this script.
     *
     * @return A list of initializers.
     **/

    public List getInitializers() {
        List result = new ArrayList();
        result.addAll(m_initializers);
        return result;
    }

    /**
     * Starts up all initializers that this script contains.
     **/

    public Collection startup() throws InitializationException {
        return startup(null);
    }

    /**
     * Starts up the specified initializer and any initializers it requires in
     * order to start.
     *
     * @param iniName The name of the initializer last to start. Note: 
     * This parameter is redundant, as if it is set in the constructor, 
     * only initializers up to the final one will be parsed.
     *
     * @return A Collection containing the names of all initalizers run
     **/

    public Collection startup(String iniName) throws InitializationException {
        if (m_isStarted) {
            throw new InitializationException(
                "Startup has already been called."
            );
        }

        HashSet initializersRun = new HashSet();
        boolean loggerIsInitialized = false;
        Initializer ini = null;
        try {
            for (int i = 0; i < m_initializers.size(); i++) {
                ini = (Initializer) m_initializers.get(i);
                if (loggerIsInitialized) {
                    s_log.info("Running initializer " + ini.getClass().getName() +
                               " (" + i + " of " + m_initializers.size() + 
                               " complete)");
                }

                final String name = ini.getClass().getName();
                if (com.arsdigita.logging.Initializer.class.getName().equals(name)) {
                    loggerIsInitialized = true;
                }

                ini.startup();
                initializersRun.add(name);
                if (name.equals(iniName)) {
                    break;
                }
            }

        } catch(Throwable t) {
            logInitializationFailure(ini, loggerIsInitialized, t);
            throw new InitializationException(
                "Initialization Script startup error!", t
            );
        }
        m_isStarted = true;
        s_log.info("Initialization Complete");
        return initializersRun;
    }

    /**
     * Shuts down all initializers that this script contains.
     **/

    public void shutdown() throws InitializationException {
        shutdown(null);
    }

    /**
     * Shuts down the specified initializer and any initializers it required
     * in order to start.
     *
     * @param iniName The name of the initializer to stop.
     **/

    public void shutdown(String iniName) throws InitializationException {
        if (m_isShutdown) {
            throw new InitializationException(
                "Shutdown has already been called."
            );
        }
        if (!m_isStarted) {
            throw new InitializationException(
                "Startup hasn't been called yet."
            );
        }

        boolean shutdown = false;
        if (iniName == null) {
            shutdown = true;
        }
        for (int i = m_initializers.size() - 1; i >= 0; i--) {
            Initializer ini = (Initializer) m_initializers.get(i);

            if (ini.getClass().getName().equals(iniName)) {
                shutdown = true;
            }

            if (shutdown) {
                ini.shutdown();
            }
        }
        m_isShutdown = true;
    }

    protected void finalize() throws Throwable {
        try {
            if (m_isStarted && !m_isShutdown) {
                try {
                    shutdown();
                } catch (Throwable t) {
                    s_log.error("Error in Script.finalize:");
                    t.printStackTrace(System.err);
                }
            }

        } finally {
            super.finalize();
        }


    }

    private void logInitializationFailure(Initializer initializer,
                                          final boolean loggerIsInitialized, 
                                          Throwable t) {
        InitializerErrorReport report = new InitializerErrorReport(t, initializer);
        
        String msg = "Fatal error loading initialization script";
        if (!loggerIsInitialized) {
            BasicConfigurator.configure();
        }
        report.logit();
    }
}
