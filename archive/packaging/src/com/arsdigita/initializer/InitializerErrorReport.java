/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.logging.ErrorReport;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This is an advanced servlet error report generator
 * which dumps practically all the information it can
 * find about the servlet request to the logs. It also
 * sets a request attribute containing the ACS Error Report
 * (guru meditation) code.
 */
public class InitializerErrorReport extends ErrorReport {
    
    private Initializer m_initializer;

    public InitializerErrorReport(Throwable throwable,
                                  Initializer initializer) {
        super(throwable);
        
        m_initializer = initializer;
        
        // Take great care such that if something goes
        // wrong while creating the error report, we don't
        // let the new exception propagate thus loosing the
        // one we're actually trying to report on.
        try {
            addInitializer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addInitializer() {
        if (m_initializer == null) {
            return;
        }

        ArrayList lines = new ArrayList();

        lines.add("Classname: "+ m_initializer.getClass().getName());

        addSection("Initializer summary", lines);
    }

    private void addConfig() {
        if (m_initializer == null) {
            return;
        }

        ArrayList lines = new ArrayList();
        
        Configuration config = m_initializer.getConfiguration();
        Iterator params = config.getParameterNames().iterator();
        while (params.hasNext()) {
            String name = (String)params.next();
            Object value = config.getParameter(name);
            
            lines.add(name + ": " + value);
        }

        addSection("Initializer parameters", lines);
    }
}
