/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.util;

import javax.servlet.http.*;
import java.util.*;
import javax.servlet.*;
import com.arsdigita.dispatcher.RequestContext;

/**
 *  Dummy RequestContext object for unit testing of form methods that
 *  include requests in their signatures.
 *
 * @version $Revision: #5 $ $Date: 2004/04/07 $
 */

public class DummyRequestContext implements RequestContext {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/DummyRequestContext.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private ServletContext m_servletContext;
    private HttpServletRequest m_request;
    private boolean m_debugging;
    private boolean m_debuggingXML;
    private boolean m_debuggingXSL;

    public DummyRequestContext(HttpServletRequest request,
                               ServletContext servletContext) {
        this(request, servletContext, true);
    }

    public DummyRequestContext(HttpServletRequest request,
                               ServletContext servletContext, boolean isDebug) {
        m_request = request;
        m_servletContext = servletContext;
        m_debugging = isDebug;
    }

    /**
     * @return the portion of the URL that has not been used yet
     * by a previous dispatcher in the chain, and must be used by
     * the current dispatcher
     */
    public String getRemainingURLPart() {
        return null;
    }

    /**
     * @return the portion of the URL that has already been used
     * by previous dispatchers in the chain.
     */
    public String getProcessedURLPart() {
        return null;
    }

    /**
     * @return The original URL requested by the end-user's browser.
     * All generated HREF, IMG SRC, and FORM ACTION attributes will
     * be relative to this URL, as will redirects.
     */
    public String getOriginalURL() {
        return m_request.getRequestURI();
    }

    /**
     * @return the current servlet context; must be set by implementation.
     */
    public ServletContext getServletContext() {
        return null;
    }

    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    public String getOutputType() {
        return null;
    }

    public boolean getDebugging() {
        return m_debugging;
    }

    public boolean getDebuggingXML() {
        return m_debuggingXML;
    }

    public boolean getDebuggingXSL() {
        return m_debuggingXSL;
    }

    public String getPageBase() {
        return null;
    }

}
