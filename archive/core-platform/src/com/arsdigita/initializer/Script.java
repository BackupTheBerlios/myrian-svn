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

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Script
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/09/09 $
 */

public class Script {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/initializer/Script.java#5 $ by $Author: jorris $, $DateTime: 2002/09/09 17:12:20 $";

    private static final Logger s_log =
        Logger.getLogger(Script.class);

    private List m_initializers = new ArrayList();
    private String iniName;
    private boolean isStarted = false;
    private boolean isShutdown = false;

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

    public Script(Reader r, String iniName) throws InitializationException {
        this.iniName = iniName;
        ScriptParser sp = new ScriptParser(r);
        try {
            sp.parse(this);
        } catch (ParseException e) {
            Token errTok = e.currentToken.next;
            throw new InitializationException(e.getMessage());
        }
    }

    /**
     * Adds an initializer to the script.
     *
     * @param ini The initializer.
     **/

    public boolean addInitializer(Initializer ini)
        throws InitializationException {
        if (isStarted)
            throw new InitializationException(
                                              "This script has already been started."
                                              );
        m_initializers.add(ini);
        if (ini.getClass().getName().equals(iniName)) {
            // stop here
            return false;
        }
        return true;
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
     * @param iniName The name of the initializer to start.
     **/

    public Collection startup(String iniName) throws InitializationException {
        if (isStarted)
            throw new InitializationException(
                                              "Startup has already been called."
                                              );
        HashSet initializersRun = new HashSet();
        try {
            for (int i = 0; i < m_initializers.size(); i++) {
                Initializer ini = (Initializer) m_initializers.get(i);
                final String name = ini.getClass().getName();
                ini.startup();
                initializersRun.add(name);
                if (name.equals(iniName))
                    break;
            }

        } catch(Exception e) {
            final boolean loggerIsInitialized = initializersRun.contains("com.arsdigita.logging.Initializer");
            logInitializationFailure(loggerIsInitialized, e);
            throw new InitializationException("Initialization Script startup error!", e);
        }
        isStarted = true;
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
        if (isShutdown)
            throw new InitializationException(
                                              "Shutdown has already been called."
                                              );
        if (!isStarted)
            throw new InitializationException(
                                              "Startup hasn't been called yet."
                                              );

        boolean shutdown = false;
        if (iniName == null)
            shutdown = true;
        for (int i = m_initializers.size() - 1; i >= 0; i--) {
            Initializer ini = (Initializer) m_initializers.get(i);

            if (ini.getClass().getName().equals(iniName))
                shutdown = true;

            if (shutdown) ini.shutdown();
        }
        isShutdown = true;
    }

    protected void finalize() throws Throwable {
        try {
            if (isStarted && !isShutdown) {
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

    private void logInitializationFailure(final boolean loggerIsInitialized, Exception e) {
        String msg = "Fatal error loading initialization script";
        if (loggerIsInitialized) {
            Logger log = Logger.getLogger(Script.class);
            log.fatal(msg, e);
        } else {
            System.err.println(msg + ":" + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

}
