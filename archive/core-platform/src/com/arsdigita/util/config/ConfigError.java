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
 * An error to indicate invalid configurations.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/config/ConfigError.java#1 $
 */
public class ConfigError extends Error {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/config/ConfigError.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

    private static final Logger s_log = Logger.getLogger
        (ConfigError.class);

    /**
     * Constructs a new configuration error with the content
     * <code>message</code>.
     *
     * @param message A <code>String</code> describing what's wrong;
     * it cannot be null
     */
    public ConfigError(final String message) {
        super(message);

        Assert.exists(message, String.class);
    }

    /**
     * Constructs a new configuration error with a default message.
     */
    public ConfigError() {
        super("Configuration is invalid");
    }
}
