/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.servlet;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;

/**
 * This class represents info about a single host running
 * a server in a webapp cluster.
 */
public class HttpHostParameter extends StringParameter {
    private static final Logger s_log = Logger.getLogger
        (HttpHostParameter.class);

    public HttpHostParameter(final String name) {
        super(name);
    }

    public HttpHostParameter(final String name,
                             final int multiplicity,
                             final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        if (value.indexOf("://") != -1) {
            final ParameterError error = new ParameterError
                (this, "The value must not have a scheme prefix");
            errors.add(error);
        }

        if (value.indexOf("/") != -1) {
            final ParameterError error = new ParameterError
                (this, "The value must not contain slashes");
            errors.add(error);
        }

        final int sep = value.indexOf(":");

        if (sep == -1) {
            final ParameterError error = new ParameterError
                (this, "The value must contain a colon");
            errors.add(error);
        }

        if (!errors.isEmpty()) {
            return null;
        }

        try {
            final String name = value.substring(0, sep);
            final String port = value.substring(sep + 1);

            return new HttpHost(name, Integer.parseInt(port));
        } catch (IndexOutOfBoundsException ioobe) {
            final ParameterError error = new ParameterError
                (this, "The host spec is invalid; it must take the form " +
                 "hostname:hostport");
            errors.add(error);

            return null;
        } catch (NumberFormatException nfe) {
            final ParameterError error = new ParameterError
                (this, "The port number must be an integer with no " +
                 "extraneous spaces or punctuation");
            errors.add(error);

            return null;
        }
    }

    protected String marshal(Object value) {
        if (value == null) {
            return null;
        } else {
            final HttpHost host = (HttpHost) value;
            return host.getName() + ":" + host.getPort();
        }
    }
}
