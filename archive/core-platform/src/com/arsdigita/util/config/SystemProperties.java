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

package com.arsdigita.util.config;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/config/SystemProperties.java#1 $
 */
public final class SystemProperties {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/config/SystemProperties.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/09 14:53:22 $";

    private static final Logger s_log = Logger.getLogger
        (SystemProperties.class);

    private static final ParameterStore s_store = new JavaPropertyStore
        (System.getProperties());

    /**
     * Uses <code>param</code> to decode, validate, and return the
     * value of a Java system property.
     *
     * @param param The <code>Parameter</code> representing the type
     * and name of the field you wish to recover
     * @return A value that may be cast to the type enforced by the
     * parameter
     */
    public static final Object get(final Parameter param) {
        final ParameterValue value = param.unmarshal(s_store);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " + value.getErrors());
        }

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " + value.getErrors());
        }

        return value.getValue();
    }
}
