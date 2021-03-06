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

package com.arsdigita.util;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.net.URLEncoder;
import org.apache.log4j.Logger;

/**
 * Re-writes URLs to include additional parameters that come from a
 * set of registered <code>ParameterProviders</code>.  This makes
 * cookieless login possible, by re-writing URLs to include a session
 * ID parameter.
 */
public class URLRewriter {
    public static final String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/URLRewriter.java#2 $" +
        "$Author: rhs $" +
        "$DateTime: 2003/08/19 22:28:24 $";

    private static final Logger s_log =
        Logger.getLogger(URLRewriter.class);

    /**
     * The parameter providers for the system.
     **/
    private static LinkedList s_providers = new LinkedList();

    /**
     * Adds a parameter provider.
     **/
    public static void addParameterProvider(ParameterProvider provider) {
        s_log.debug("addParameterProvider: "
                    + provider.getClass().getName());
        s_providers.add(provider);
    }

    /**
     * Clears all parameter providers.
     **/
    public static void clearParameterProviders() {
        s_providers = new LinkedList();
    }
    /**
     * Returns the set of global parameter models, or the empty set if no
     * provider is set.
     *
     * @return a set of bebop ParameterModels
     **/
    public static Set getGlobalModels() {
        if (s_providers.isEmpty()) {
            s_log.debug("getGlobalModels: no providers set");
            return java.util.Collections.EMPTY_SET;
        }

        Set rs = new HashSet();
        for (Iterator i = s_providers.iterator(); i.hasNext();) {
            rs.addAll(((ParameterProvider) i.next()).getModels());
        }
        return rs;
    }

    /**
     * Returns the set of global URL parameters for the given request, or
     * the empty set if no provider is set.
     *
     * @return a set of bebop ParameterData
     **/
    public static Set getGlobalParams(HttpServletRequest req) {
        if (s_providers.isEmpty()) {
            s_log.debug("getGlobalParams: no providers set");
            return java.util.Collections.EMPTY_SET;
        }

        Set rs = new HashSet();
        for (Iterator i = s_providers.iterator(); i.hasNext();) {
            rs.addAll(((ParameterProvider)i.next()).getParams(req));
        }
        return rs;
    }

    /**
     * Encodes the given URL for redirecting the client.  Adds ACS global
     * parameters and servlet session parameters to the URL.  The
     * sendRedirect(req, resp, url) method calls this method automatically.
     *
     * @return the new URL
     **/
    public static String encodeRedirectURL(HttpServletRequest req,
                                           HttpServletResponse resp,
                                           String url) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("encodeRedirectURL: before: " + url);
        }

        url = resp.encodeRedirectURL(encodeParams(req, url));

        if (s_log.isDebugEnabled()) {
            s_log.debug("encodeRedirectURL:  after: " + url);
        }

        return url;
    }

    /**
     * Prepares the given URL for the client.  No effect if no provider is
     * set.
     *
     * @return the prepared URL
     *
     * @deprecated This method does not encode the servlet session ID.  Use
     * encodeURL(req, res, url) instead.
     **/
    public static String prepareURL(String url, HttpServletRequest req) {
        return encodeParams(req, url);
    }

    /**
     * Encodes the given URL for the client.  Adds ACS global parameters and
     * servlet session parameters to the URL.  If the URL will be used for
     * redirection, use sendRedirect(req, resp, url) instead.
     *
     * @return the new URL
     **/
    public static String encodeURL(HttpServletRequest req,
                                   HttpServletResponse resp,
                                   String url) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("encodeURL: before: " + url);
        }

        url = resp.encodeURL(encodeParams(req, url));

        if (s_log.isDebugEnabled()) {
            s_log.debug("encodeURL:  after: " + url);
        }

        return url;
    }

    /**
     * Adds the ACS global params to the URL.
     **/
    private static String encodeParams(HttpServletRequest req, String url) {
        if (s_providers.isEmpty()) {
            s_log.debug("encodeParams: no providers set");
            return url;
        }
        Map params = new java.util.HashMap();
        String base = parseQueryString(url, params);
        merge(getGlobalParams(req), params);
        url = base + unparseQueryString(params);
        return url;
    }

    /**
     * Merges a set of bebop ParameterData into a URL parameter map.
     **/
    private static void merge(Set data, Map params) {
        Iterator values = data.iterator();
        while (values.hasNext()) {
            Map.Entry value = (Map.Entry)values.next();
            if (value == null) {
                continue;
            }
            params.put(value.getKey(), value.getValue());
        }
    }

    /**
     * Parses the given URL into a non-query part and a URL parameter map.
     *
     * @param url the original URL
     *
     * @param params map from param name to value.  Each value is a String
     * if parameter is single-valued; String[] if multi-valued.  Existing
     * values will be blindly overwritten by this method.
     *
     * @return the non-query part of the URL
     **/
    private static String parseQueryString(String url, Map params) {
        int qmark = url.indexOf('?');
        if (qmark < 0) {
            return url;
        }
        String base = url.substring(0, qmark);
        String query = url.substring(qmark+1);
        params.putAll(HttpUtils.parseQueryString(query));
        return base;
    }

    /**
     * Returns the query string representation of the given URL parameter
     * map, including leading question mark.  Should be appended to the
     * return value of a previous call to parseQueryString().  Handles
     * multi-valued parameters correctly.  Ignores null values.
     **/
    private static String unparseQueryString(Map params) {
        StringBuffer buf = new StringBuffer(128);
        char sep = '?';
        Iterator keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String)keys.next();
            Object value = params.get(key);
            if (value instanceof String[]) {
                String[] values = (String[])value;
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        appendParam(buf, sep, key, values[i]);
                        sep = '&';
                    }
                }
                continue;
            } else if (value != null) {
                appendParam(buf, sep, key, value.toString());
                sep = '&';
            }
        }
        return buf.toString();
    }

    /**
     * Appends string representation of a parameter to the given
     * StringBuffer: sep + URLEncode(key) + '=' + URLEncode(value)
     **/
    private static void appendParam(StringBuffer buf, char sep,
                                    String key, String value) {
        buf.append(sep).append(URLEncoder.encode(key))
            .append('=').append(URLEncoder.encode(value));
    }
}
