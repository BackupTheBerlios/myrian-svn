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
 * A base class for defining configuration records.  It uses {@link
 * com.arsdigita.util.parameter parameters} to recover configuration
 * from a persistent store.
 *
 * @see com.arsdigita.util.parameter.ParameterStore
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#5 $
 */
public class BaseConfig {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#5 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/11 11:20:25 $";

    private static final Logger s_log = Logger.getLogger
        (BaseConfig.class);

    private final ParameterStore m_store;

    /**
     * Constructs a configuration record that uses <code>store</code>
     * to marshal and unmarshal parameter values.
     *
     * @param store The <code>ParameterStore</code> to keep the
     * parameter values in
     */
    protected BaseConfig(final ParameterStore store) {
        m_store = store;
    }

    /**
     * Constructs a configuration record that uses the Java properties
     * file referenced by <code>resource</code> as its store.  If no
     * such resource is found, an empty properties file is used.
     *
     * @param resource The location on the class path of a Java
     * properties file; it cannot be null
     */
    protected BaseConfig(final String resource) {
        Assert.exists(resource, String.class);

        final Properties props = new Properties();

        final InputStream in = getClass().getResourceAsStream(resource);

        if (in == null) {
            if (s_log.isInfoEnabled()) {
                s_log.info(resource + " was not found; using an empty " +
                           "property record");
            }
        } else {
            try {
                props.load(in);
            } catch (IOException ioe) {
                throw new UncheckedWrapperException(ioe);
            }
        }

        m_store = new JavaPropertyStore(props);
    }

    /**
     * Initializes a named parameter (represented by
     * <code>param</code> by unmarshaling and validating it.  If any
     * errors are encountered during the unmarshal or validate steps,
     * a <code>ConfigurationError</code> listing the error messages is
     * thrown.
     *
     * @throws com.arsdigita.util.config.ConfigurationError
     * @param param The named <code>Parameter</code> you wish to
     * fetch; it cannot be null
     * @return The unmarshaled value of <code>param</code>; it may be
     * null
     */
    protected final Object initialize(final Parameter param) {
        Assert.exists(param, Parameter.class);

        // 1. Unmarshal and check for errors

        final ParameterValue value = param.unmarshal(m_store);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        // 2. Validate and check for errors

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        return value.getValue();
    }
}
