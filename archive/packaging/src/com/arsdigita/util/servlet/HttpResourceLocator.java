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

package com.arsdigita.util.servlet;

import com.arsdigita.util.*;
import javax.servlet.http.*;
import java.net.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * <p>URL models a future request according to the servlet worldview.
 * Its principal uses are two:
 *
 *   <ul>
 *     <li>To expose all the parts of a URL.  To a servlet's way of
 *     thinking, these are the scheme, server name, server port,
 *     context path, servlet path, path info, and parameters.</li>
 *
 *     <li>To generate URLs in a consistent and complete way in one
 *     place.</li>
 *   </ul>
 * </p>
 *
 * <p>Each URL has the following accessors, here set next to an
 * example URL instance,
 * <code>http://example.com:8080/ccmapp/forum/index.jsp?cat=2&cat=5</code>:</p>
 *
 * <p>Atomic parts:
 *
 * <pre><blockquote>
 *               getScheme() -> "http"
 *           getServerName() -> "example.com"
 *           getServerPort() -> 8080
 *          getContextPath() -> "/ccmapp"
 *          getServletPath() -> "/forum"
 *             getPathInfo() -> "/index.jsp"
 *       getParameter("cat") -> "2"
 * getParameterValues("cat") -> {"2", "5"}
 * </blockquote></pre>
 *
 * </p>
 *
 * <p>Composite parts:
 *
 * <pre><blockquote>
 *                toString() -> "/ccmapp/forum/index.jsp?cat=2&cat=5"
 *                  getURL() -> "http://example.com:8080/ccmapp/forum/index.jsp?cat=2&cat=5
 *            getServerURI() -> "http://example.com:8080"   // No trailing "/"
 *           getRequestURI() -> "/ccmapp/forum/index.jsp"
 *          getQueryString() -> "cat=2&cat=5"               // No leading "?"
 *         getParameterMap() -> {cat={"2", "5"}}
 * </blockquote></pre>
 *
 * </p>
 *
 * <p>The <code>toString()</code> method returns a URL suitable for
 * use in hyperlinks; since in the common case, the scheme, server
 * name, and port are best left off, <code>toString()</code> omits
 * them.  The <code>getURL()</code> method returns a
 * <code>String</code> URL which is fully qualified.  Both
 * <code>getURL()</code> and <code>getServerURI()</code> omit the port
 * from their return values if the server port is the default, port
 * 80.</p>
 *
 * <p>Creating URLs will usually be done via one of the static create
 * methods:</p>
 *
 * <p><code>URL.root()</code> creates a URL pointing at the server's
 * root path, "/".</p>
 *
 * <p><code>URL.request(req, params)</code> creates a URL reflecting
 * the request the client made but using the passed-in parameters
 * instead.</p>
 *
 * <p><code>URL.there(req, path, params)</code> and its variants
 * produce URLs that go through the CCM main dispatcher.  The variant
 * <code>URL.there(req, app, pathInfo, params)</code> dispatches to
 * <code>pathInfo</code> under the specified application.  The variant
 * <code>URL.here(req, pathInfo, params)</code> dispatches to
 * <code>pathInfo</code> under the current application.</p>
 *
 * <p><code>URL.excursion(req, path, params)</code> produces URLs that
 * go through the dispatcher to a destination but also encode and
 * store the origin.  This is used by <code>LoginSignal</code> and
 * <code>ReturnSignal</code> to implement UI excursions.</p>
 *
 * <p>All static create methods taking an
 * <code>HttpServletRequest</code> (1) preserve the request's scheme,
 * server name, and port and (2) run parameter listeners if the URL's
 * parameter map is not null.</p>
 *
 * <p>Those methods not taking an <code>HttpServletRequest</code> use
 * the scheme, server name, and port defined in
 * <code>WebConfig</code>.</p>
 *
 * <p>All static create methods taking a <code>ParameterMap</code>
 * take null to mean no query string at all.  URLs defined this way
 * will have no query string and no "?".</p>
 *
 * <p>Those methods not taking a <code>ParameterMap</code> argument implicitly
 * create an empty parameter map.  Note that this is different from
 * creating a URL with a null parameter map, which produces a URL with
 * no query string.</p>
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpResourceLocator.java#2 $
 */
public class HttpResourceLocator {
    public static final String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpResourceLocator.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/03 22:51:23 $";

    private static final Logger s_log = Logger.getLogger
        (HttpResourceLocator.class);

    private final HttpHost m_host;
    private final String m_contextPath;
    private final String m_servletPath;
    private final String m_pathInfo;
    private final HttpParameterMap m_params;

    /**
     * Assembles a fully qualified URL from its fundamental pieces.
     * The contract of URL dictates that once <code>host</code> and
     * <code>params</code> are passed in to this constructor, they
     * should not be changed.  This is to make
     * <code>HttpResourceLocator</code> in practice a read-only
     * object.
     *
     * @param host A <code>HttpHost</code> object representing the
     * requested host
     *
     * @param contextPath The path to your web app; empty string
     * indicates the default context; any other values for contextPath
     * must start with <code>"/"</code> but not end in
     * <code>"/"</code>; contextPath cannot be null; see {@link
     * javax.servlet.http.HttpServletRequest#getContextPath()}
     *
     * @param servletPath The path to your servlet; empty string and
     * values starting with <code>"/"</code> are valid, but null is
     * not; see {@link
     * javax.servlet.http.HttpServletRequest#getServletPath()}
     *
     * @param pathInfo The path data remaining after the servlet path
     * but before the query string; pathInfo may be null; see {@link
     * javax.servlet.http.HttpServletRequest#getPathInfo()}
     *
     * @param params An <code>HttpParameterMap</code> representing a
     * set of query parameters
     */
    public HttpResourceLocator(final HttpHost host,
                               final String contextPath,
                               final String servletPath,
                               final String pathInfo,
                               final HttpParameterMap params) {
        if (Assert.isAssertEnabled()) {
            Assert.exists(host, HttpHost.class);
            Assert.exists(contextPath, String.class);
            Assert.exists(servletPath, String.class);

            if (contextPath.startsWith("/")) {
                Assert.truth
                    (!contextPath.endsWith("/"),
                     "A contextPath starting with '/' must not end in '/'; " +
                     "I got '" + contextPath + "'");
            }

            if (pathInfo != null) {
                Assert.truth(pathInfo.startsWith("/"),
                             "I expected a pathInfo starting with '/' " +
                             "and got '" + pathInfo + "' instead");
            }
        }

        m_host = host;
        m_contextPath = contextPath;
        m_servletPath = servletPath;
        m_pathInfo = pathInfo;
        m_params = params;
    }

    /**
     * Produces a URL from <code>host</code>, <code>sreq</code>, and
     * <code>params</code>.  The servlet request is mined for its
     * context path, servlet path, and path info.
     *
     * @param host The <code>HttpHost</code> being addressed; it
     * cannot be null
     * @param sreq An <code>HttpServletRequest</code> from which to
     * copy; it cannot be null
     * @param params A <code>HttpParameterMap</code> of query parameters;
     * it can be null
     */
    public HttpResourceLocator(final HttpHost host,
                               final HttpServletRequest sreq,
                               final HttpParameterMap params) {
        this(host,
             sreq.getContextPath(),
             sreq.getServletPath(),
             sreq.getPathInfo(),
             params);
    }

    /**
     * Produces a URL representation of <code>sreq</code>.
     *
     * @param sreq An <code>HttpServletRequest</code> from which to
     * copy; it cannot be null
     */
    public HttpResourceLocator(final HttpServletRequest sreq) {
        this(new HttpHost(sreq), sreq, new HttpParameterMap(sreq));
    }

    /**
     * <p>Produces a short description of a URL suitable for
     * debugging.</p>
     *
     * @return a debugging representation of this URL
     */
    public final String toDebugString() {
        return super.toString() + " " +
            "[" +
            m_host + "," +
            getContextPath() + "," +
            getServletPath() + "," +
            getPathInfo() + "," +
            m_params +
            "]";
    }

    /**
     * Returns a <code>URL</code> to the resource, fully qualified.
     *
     * @return A <code>URL</code> for accessing this resource
     */
    public final URL toURL() {
        try {
            return new URL(toString());
        } catch (MalformedURLException murle) {
            throw new UncheckedWrapperException(murle);
        }
    }

    /**
     * Returns the virtual host of the resource location.
     *
     * @see javax.servlet.ServletRequest#getServerName()
     * @see javax.servlet.ServletRequest#getServerPort()
     * @return a <code>HttpHost</code> representing the resource's
     * host name and port number
     */
    public final HttpHost getHost() {
        return m_host;
    }

    /**
     * Returns the context path of the resource.  The value cannot be
     * null, and values starting with <code>"/"</code> do not end in
     * <code>"/"</code>; empty string is a valid return value that
     * stands for the default web app.  Example values are
     * <code>""</code> and <code>"/ccm-app"</code>.
     *
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     * @return A <code>String</code> path to a webapp context; it
     * cannot be null
     */
    public final String getContextPath() {
        return m_contextPath;
    }

    /**
     * Returns the servlet path of the resource.  The value cannot be
     * null.
     *
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     * @return a <code>String</code> path to a servlet; it cannot be
     * null
     */
    public final String getServletPath() {
        return m_servletPath;
    }

    /**
     * Returns the servlet-local path data of the resource location.
     * The value may be null.  If it is not null, the value begins
     * with a "/".  Examples are <code>null</code>, <code>"/"</code>,
     * and <code>"/remove.jsp"</code>.
     *
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     * @return A <code>String</code> of path data addressed to a
     * servlet; it can be null
     */
    public final String getPathInfo() {
        return m_pathInfo;
    }

    /**
     * Returns an immutable map of the query parameters.  The map's
     * keys are <code>String</code>s and the map's values are
     * <code>String[]</code>s.  If the URL was constructed with a null
     * <code>HttpParameterMap</code>, this method returns null.
     *
     * @see javax.servlet.http.HttpServletRequest#getHttpParameterMap()
     * @return a <code>Map</code> of the URL's query parameters
     */
    public final Map getParameterMap() {
        if (m_params == null) {
            return null;
        } else {
            return m_params.getParameterMap();
        }
    }

    final void toString(final StringBuffer buffer) {
	buffer.append("http://");

        m_host.toString(buffer);

        buffer.append(m_contextPath);
        buffer.append(m_servletPath);

        if (m_pathInfo != null) {
            buffer.append(m_pathInfo);
        }

	if (m_params != null) {
	    m_params.toString(buffer);
	}
    }

    /**
     * Returns a <code>String</code> representation of the URL
     * suitable for use as a hyperlink.  The scheme, server name, and
     * port are omitted.
     *
     * @return a <code>String</code> URL
     */
    public final String toString() {
        final StringBuffer buffer = new StringBuffer(96);

        toString(buffer);

        return buffer.toString();
    }
}
