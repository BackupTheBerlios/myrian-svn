/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * A (static) class of generally-useful Java servlet utilities.
 * @author Bill Schneider
 */

public class ServletUtils {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/ServletUtils.java#8 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

    private ServletUtils() { }

    /**
     * Returns a string that can be appended to a URL as a query string.
     * This exports URL variables and their values in the current request to
     * for use in a new request.  <p>
     * If request contains the variables
     * "one"=1, "two"=2, and "three"=3, then exportURLVars(req, "one two")
     *  will return "one=1&two=2".
     * @param req the HttpServletRequest
     * @param vars a space-separated list of variables to export.  If vars
     * is null, export all available.
     */
    public static final String exportURLVars(HttpServletRequest req,
                                             String vars) {
        boolean firstTime = true;
        StringBuffer buf = new StringBuffer();
        String[] varArray;
        if (vars != null) {
            varArray = StringUtils.split(vars, ' ');
        } else {
            List varList = new ArrayList();
            Enumeration enum = req.getParameterNames();
            while (enum.hasMoreElements()) {
                varList.add(enum.nextElement());
            }
            varArray = new String[varList.size()];
            varList.toArray(varArray);
        }
        for (int i = 0; i < varArray.length; i++) {
            String key = varArray[i];
            String value = req.getParameter(key);
            if (value != null) {
                if (! firstTime) {
                    buf.append('&');
                }
                buf.append(key);
                buf.append('=');
                buf.append(URLEncoder.encode(value));
                firstTime = false;
            }
        }
        return buf.toString();
    }


    /**
     * Returns a cookie value as a String, given a cookie name.
     * @param request The servlet request
     * @param withName The cookie name
     * @return The cookie value
     * @see javax.servlet.http.Cookie
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    public final static String getCookieValue(HttpServletRequest request,
                                              String withName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (int i = 0; i < cookies.length; i++) {
            Cookie c = cookies[i];
            if (c.getName().equals(withName)) {
                return c.getValue();
            }
        }
        return null;
    }
}
