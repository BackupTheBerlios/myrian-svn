/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.util.Assert;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * Static convenience methods for dealing with cookies.
 *
 * <a href="http://wp.netscape.com/newsref/std/cookie_spec.html">http://wp.netscape.com/newsref/std/cookie_spec.html</a>
 *
 * <a href="http://www.faqs.org/rfcs/rfc2109.html">http://www.faqs.org/rfcs/rfc2109.html</a>
 *
 * @see javax.servlet.http.HttpServletRequest#getCookies()
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/servlet/Cookies.java#1 $
 */
public class Cookies {
    public static final String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/servlet/Cookies.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private static final Logger s_log = Logger.getLogger(Cookies.class);

    private Cookies() {
	// Empty
    }

    /**
     * Gets the cookie whose name is <code>name</code> from the
     * request.  If there is no such cookie, we return null.
     *
     * @param sreq The request from which to fetch the cookie
     * @param name The <code>String</code> key to use to look up the
     * cookie
     * @return The <code>Cookie</code> whose name is <code>name</code>
     * or null if there is no such cookie
     */
    public static final Cookie get(final HttpServletRequest sreq,
				   final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sreq, HttpServletRequest.class);
            Assert.exists(name, String.class);
        }

        final Cookie[] cookies = sreq.getCookies();

        if (cookies == null) {
            return null;
        } else {
            for (int i = 0; i < cookies.length; i++) {
                final Cookie cookie = cookies[i];

                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }

            return null;
        }
    }

    /**
     * Gets the <code>String</code> value of a cookie with
     * <code>name</code> from the request object <code>sreq</code>.
     * If there is no such cookie, <code>null</code> is returned.
     *
     * @param sreq The request object
     * @param name The <code>String</code> name of the cookie to look
     * for
     * @return The <code>String</code> value of the cookie or
     * <code>null</code> if it is not present on <code>sreq</code>
     */
    public static final String getValue(final HttpServletRequest sreq,
					final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sreq, HttpServletRequest.class);
            Assert.exists(name, String.class);
        }

	final Cookie cookie = Cookies.get(sreq, name);

	if (cookie == null) {
	    return null;
	} else {
	    return cookie.getValue();
	}
    }

    /**
     * Sets a cookie on the response with the given expiration time.
     * This method generates a cookie with the defaults of {@link
     * javax.servlet.http.Cookie#Cookie(String, String)}.  The cookie
     * object is returned to allow custom settings.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to set
     * @param value The <code>String</code> value of the cookie to set
     * @return The <code>Cookie</code> instance created and set by
     * this method
     */
    public static final Cookie set(final HttpServletResponse sresp,
				   final String name,
				   final String value,
				   final int expiry) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
            Assert.exists(value, String.class);
        }

        final Cookie cookie = Cookies.set(sresp, name, value);

        cookie.setMaxAge(expiry);

        return cookie;
    }

    /**
     * Sets a session cookie on the response.  This method generates a
     * cookie with the defaults of {@link
     * javax.servlet.http.Cookie#Cookie(String, String)}.  The cookie
     * object is returned to allow custom settings.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to set
     * @param value The <code>String</code> value of the cookie to set
     * @return The <code>Cookie</code> instance created and set by
     * this method
     */
    public static final Cookie set(final HttpServletResponse sresp,
				   final String name,
				   final String value) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
            Assert.exists(value, String.class);
        }

        final Cookie cookie = new Cookie(name, value);

	// XXX Deal with the case where it's already been added.

        sresp.addCookie(cookie);

        return cookie;
    }

    /**
     * Deletes the named cookie by setting its maximum age to 0.
     *
     * @param sresp The response object
     * @param name The <code>String</code> name of the cookie to
     * delete
     */
    public static final void delete(final HttpServletResponse sresp,
				    final String name) {
        if (Assert.isEnabled()) {
            Assert.exists(sresp, HttpServletResponse.class);
            Assert.exists(name, String.class);
        }

        Cookies.set(sresp, name, "", 0);
    }
}
