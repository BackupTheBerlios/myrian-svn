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
import com.arsdigita.util.config.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#1 $
 */
public class BaseConfig {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/27 12:40:24 $";

    private static final Logger s_log = Logger.getLogger
        (BaseConfig.class);

    private final FilePropertyStore m_store;

    protected BaseConfig(final String filename) {
        m_store = new FilePropertyStore(filename);
    }

    protected final Object initialize(final Parameter param,
                                      final Object defaalt) {
        // 1. Unmarshal and check for errors

        final ParameterValue value = param.unmarshal(m_store);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError(value.getErrors().toString());
        }

        // 2. Set to default if null

        if (!value.isAssigned()) {
            value.setValue(defaalt);
        }

        // 3. Validate and check for errors

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError(value.getErrors().toString());
        }

        return value.getValue();
    }
}
