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

import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;
import com.arsdigita.util.OrderedMap;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Represents a set of HTTP form or query parameters.
 *
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpParameterMap.java#1 $
 */
public class HttpParameterMap {
    public static final String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpParameterMap.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/15 01:16:50 $";

    private static final Logger s_log = Logger.getLogger
        (HttpParameterMap.class);

    private static ArrayList s_listeners = new ArrayList();

    private final OrderedMap m_params;

    public HttpParameterMap() {
        m_params = new OrderedMap();
    }

    public HttpParameterMap(final Map params) {
        this();

        m_params.putAll(params);
    }

    public HttpParameterMap(final HttpServletRequest sreq) {
        this();

        final Enumeration keys = sreq.getParameterNames();

        while (keys.hasMoreElements()) {
            final String name = (String) keys.nextElement();
            final String[] values = (String[]) sreq.getParameterValues(name);

            setParameterValues(name, values);
        }
    }

    // Expects an *encoded* query string, just as
    // request.getQueryString() returns.
    private HttpParameterMap(final String query) {
        this();

        if (query != null) {
            parseQueryString(query);
        }
    }

    public static final void register(final HttpParameterListener listener) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering parameter listener " + listener);
        }

        s_listeners.add(listener);
    }

    private void validateName(final String name) {
        Assert.exists(name, String.class);
        Assert.truth(!name.equals(""),
                     "The name must not be the empty string");
        Assert.truth(name.indexOf(" ") == -1,
                     "The name must not contain any spaces: '" +
                     name + "'");
    }

    public final void clear() {
        m_params.clear();
    }

    public final String getParameter(final String name) {
        final String[] values = (String[]) m_params.get(name);

        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    /**
     * Sets the parameter <code>name<code> to <code>value</code>.  If
     * <code>value</code> is null, this method sets the value to the
     * empty string.
     *
     * Use of this method assumes that the parameter has only one
     * value; if you wish to give a parameter multiple values, use
     * {@link #setParameterValues(String, String[])}.
     *
     * @param name The <code>String</code> name of the parameter
     * @param value The <code>String</code> value of the parameter
     * @see javax.servlet.ServletRequest#getParameter(String)
     * @pre name != null && !name.trim().equals("")
     */
    public final void setParameter(final String name, final String value) {
        if (Assert.isEnabled()) {
            validateName(name);
        }

        if (value == null) {
            m_params.put(name, new String[] {""});
        } else {
            m_params.put(name, new String[] {value});
        }
    }

    /**
     * A convenience method that calls {@link #setParameter(String,
     * String)} using <code>value.toString()</code>.  If
     * <code>value</code> is null, it is converted to the empty
     * string.
     *
     * @param name The <code>String</code> name of the parameter
     * @param value The <code>Object</code> value of the parameter
     * @pre name != null && !name.trim().equals("")
     */
    public final void setParameter(final String name, final Object value) {
        setParameter(name, value.toString());
        if (value == null) {
            setParameter(name, "");
        } else {
            setParameter(name, value.toString());
        }
    }

    public final String[] getParameterValues(final String name) {
        return (String[]) m_params.get(name);
    }

    public final void setParameterValues(final String name,
                                         final String[] values) {
        if (Assert.isEnabled()) {
            validateName(name);
            Assert.assertNotNull(values, "String[] values");
            Assert.assertTrue(values.length > 0,
                              "The values array must have at least one value");
        }

        m_params.put(name, values);
    }

    public final void clearParameter(final String name) {
        if (Assert.isEnabled()) {
            validateName(name);
        }

        m_params.remove(name);
    }

    public final Map getParameterMap() {
        if (m_params.isEmpty()) {
            return null;
        } else {
            return Collections.unmodifiableMap(m_params);
        }
    }

    public final String toString() {
        if (m_params.isEmpty()) {
            return "";
        } else {
            return "?" + makeQueryString();
        }
    }

    public final String getQueryString() {
        return makeQueryString();
    }

    public final void runListeners(final HttpServletRequest sreq) {
        final Iterator iter = s_listeners.iterator();

        while (iter.hasNext()) {
            ((HttpParameterListener) iter.next()).run(sreq, this);
        }
    }

    final String makeQueryString() {
        final StringBuffer buffer = new StringBuffer();

        toString(buffer);

        return buffer.toString();
    }

    final void toString(final StringBuffer buffer) {
        final Iterator iter = m_params.entrySet().iterator();

        while (iter.hasNext()) {
            final Map.Entry entry = (Map.Entry) iter.next();
            final String key = (String) entry.getKey();
            final String[] values = (String[]) entry.getValue();

            if (Assert.isEnabled()) {
                Assert.truth(key.indexOf('%') == -1,
                             "The key '" + key + "' has already been " +
                             "encoded");
            }

            if (values != null) {
                if (Assert.isEnabled()) {
                    Assert.truth(values.toString().indexOf('%') == -1,
                                 "One of the values " +
                                 Arrays.asList(values) + " has " +
                                 "already been encoded");
                }

                for (int i = 0; i < values.length; i++) {
                    buffer.append(URLEncoder.encode(key));
                    buffer.append('=');

                    final String value = values[i];

                    if (value != null) {
                        buffer.append(URLEncoder.encode(value));
                    }

                    buffer.append('&');
                }
            }
        }

        final int last = buffer.length() - 1;

        if (last > -1 && buffer.charAt(last) == '&') {
            buffer.deleteCharAt(last);
        }
    }

    private void parseQueryString(final String query) {
        final int len = query.length();
        int start = 0;

        while (true) {
            int end = -1;

            for (int i = start; i < len - 1; i++) {
                if (query.charAt(i) == '&' || query.charAt(i) == ';') {
                    end = i;

                    break;
                }
            }

            if (end == -1) {
                if (len > start) {
                    parseParameter(query, start, len);
                }

                break;
            } else {
                parseParameter(query, start, end);
                start = end + 1;
            }
        }
    }

    private void parseParameter(final String query,
                                final int start,
                                final int end) {
        final int sep = query.indexOf('=', start);

        if (Assert.isAssertEnabled()) {
            Assert.assertTrue(start > -1);
            Assert.assertTrue(end > -1);
        }

        if (sep > -1) {
            final String name = URLDecoder.decode(query.substring(start, sep));
            final String value = URLDecoder.decode
                (query.substring(sep + 1, end));

            if (s_log.isDebugEnabled()) {
                s_log.debug("Parameter " + name + " = " + value);
            }

            final String[] values = getParameterValues(name);

            if (values == null) {
                setParameter(name, value);
            } else {
                final String[] newValues = new String[values.length + 1];

                for (int i = 0; i < values.length; i++) {
                    newValues[i] = values[i];
                }

                newValues[values.length] = value;

                setParameterValues(name, newValues);
            }
        }
    }
}
