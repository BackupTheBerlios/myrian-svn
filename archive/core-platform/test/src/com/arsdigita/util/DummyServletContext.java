/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util;

import org.apache.log4j.Logger;

import javax.servlet.*;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

/**
 *  Dummy ServletContext object for unit testing of form methods that
 *  include requests in their signatures.
 *
 * @version $Revision: #9 $ $Date: 2004/03/30 $
 */

public class DummyServletContext implements ServletContext {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/DummyServletContext.java#9 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private HashMap m_attributes = new HashMap();
    private HashMap m_dispachers = new HashMap();
    private static final Logger s_log = Logger.getLogger(DummyServletContext.class);


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
        String root = System.getProperty("test.webapp.dir");
        if (path.equals("/")) {
            return root;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return root + path;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public java.net.URL getResource(String path) {
        return null;
    }

    public java.io.InputStream getResourceAsStream(String name) {
        final String path = getRealPath(name);

        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            s_log.error("Couldn't get file stream for resource " + name + " at path " + path, e);
            return null;
        }
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
