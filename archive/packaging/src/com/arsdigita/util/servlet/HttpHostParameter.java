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

import com.arsdigita.domain.*;
import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.util.*;
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

    protected Object unmarshal(final String value, final List errors) {
        if (value.indexOf("://") != -1) {
            errors.add
                ("The value must not have a scheme prefix");
        }

        if (value.indexOf("/") != -1) {
            errors.add
                ("The value must not contain slashes");
        }

        final int sep = value.indexOf(":");

        if (sep == -1) {
            errors.add
                ("The value must contain a colon");
        }

        if (!errors.isEmpty()) {
            return null;
        }

        try {
            final String name = value.substring(0, sep);
            final String port = value.substring(sep + 1);

            return new HttpHost(name, Integer.parseInt(port));
        } catch (IndexOutOfBoundsException ioobe) {
            errors.add
                ("The host spec is invalid; it must take the form " +
                 "hostname:hostport");

            return null;
        } catch (NumberFormatException nfe) {
            errors.add
                ("The port number must be an integer with no extraneous " +
                 "spaces or punctuation");

            return null;
        }
    }
}
