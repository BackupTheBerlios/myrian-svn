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

package com.arsdigita.util;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 *  Dummy ServletContext object for unit testing of form methods that
 *  include requests in their signatures.
 *
 * @author <a href="mailto:richardl@arsdigita.com">richardl@arsdigita.com</a>
 * @version $Revision: #4 $ $Date: 2002/10/21 $
 */

public class DummyServletContext implements ServletContext {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/DummyServletContext.java#4 $ by $Author: jorris $, $DateTime: 2002/10/21 17:18:48 $";

    private HashMap m_attributes = new HashMap();
    private HashMap m_dispachers = new HashMap();


    public Object getAttribute(String name) {
        return null;
    }

    public java.util.Enumeration getAttributeNames() {
        return null;
    }

    public ServletContext getContext(String uripath) {
        return null;
    }

    public java.lang.String getInitParameter(String name) {
        return null;
    }

    public java.util.Enumeration getInitParameterNames() {
        return null;
    }

    public int getMajorVersion() {
        return 2;
    }

    public String getMimeType(String file) {
        return null;
    }

    public int getMinorVersion() {
        return 2;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return (RequestDispatcher) m_dispachers.get(name);
    }

    public String getRealPath(String path) {
        return null;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public java.net.URL getResource(String path) {
        return null;
    }

    public java.io.InputStream getResourceAsStream(String path) {
        return null;
    }

    public String getServerInfo() {
        return "Bogus Server/1.0";
    }

    public Servlet getServlet(String name) {
        return null;
    }

    public java.util.Enumeration getServletNames() {
        return null;
    }

    public java.util.Enumeration getServlets() {
        return null;
    }

    public void log(Exception exception, String msg) {
        return;
    }

    public void log(String msg) {
        return;
    }

    public void log(String message, Throwable throwable) {
        return;
    }

    public void removeAttribute(String name) {
        return;
    }

    public void setAttribute(String name, Object object) {
        return;
    }

    public Set getResourcePaths(String path) {
        return null;
    }

    public String getServletContextName() {
        return null;
    }

    public void addDispacher(String name, final Servlet servlet) {
        RequestDispatcher rd = new RequestDispatcher() {
            public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                servlet.service(servletRequest, servletResponse);
            }

            public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
                throw new UnsupportedOperationException("Not yet supported");
            }
        };

        m_dispachers.put(name, rd);
    }

}
