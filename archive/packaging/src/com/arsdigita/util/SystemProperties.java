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

import com.arsdigita.util.config.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * Static utility methods for handling Java system properties.
 *
 * @see java.lang.System
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/SystemProperties.java#3 $
 */
public final class SystemProperties {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/SystemProperties.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/23 11:43:44 $";

    private static final Logger s_log = Logger.getLogger
        (SystemProperties.class);

    private static final ParameterLoader s_loader = new JavaPropertyLoader
        (System.getProperties());

    /**
     * Uses <code>param</code> to decode, validate, and return the
     * value of a Java system property.
     *
     * @param param The <code>Parameter</code> representing the type
     * and name of the field you wish to recover; it cannot be null
     * @return A value that may be cast to the type enforced by the
     * parameter; it can be null
     */
    public static final Object get(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the value of " + param + " from " +
                        "the system properties");
        }

        Assert.exists(param, Parameter.class);

        final ParameterValue value = s_loader.load(param);

        if (value == null) {
            return null;
        } else {
            if (!value.getErrors().isEmpty()) {
                throw new ConfigError
                    ("Parameter " + param.getName() + ": " +
                     value.getErrors());
            }

            param.validate(value);

            if (!value.getErrors().isEmpty()) {
                throw new ConfigError
                    ("Parameter " + param.getName() + ": " +
                     value.getErrors());
            }

            return value.getObject();
        }
    }
}
