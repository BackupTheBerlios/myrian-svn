/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/ServletUtils.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
