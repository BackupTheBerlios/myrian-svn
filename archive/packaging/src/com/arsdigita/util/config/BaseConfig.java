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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#10 $
 */
public class BaseConfig {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/BaseConfig.java#10 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/15 15:51:43 $";

    private static final Logger s_log = Logger.getLogger
        (BaseConfig.class);

    private final ParameterStore m_store;
    private final ArrayList m_params;
    private final HashMap m_values;

    /**
     * Constructs a configuration record that uses <code>store</code>
     * to marshal and unmarshal parameter values.
     *
     * @param store The <code>ParameterStore</code> to keep the
     * parameter values in
     */
    protected BaseConfig(final ParameterStore store) {
        m_store = store;
        m_params = new ArrayList();
        m_values = new HashMap();
    }

    /**
     * Constructs a configuration record that uses the Java properties
     * file referenced by <code>resource</code> as its store.
     *
     * @param resource The location on the class path of a Java
     * properties file; it cannot be null
     * @param required Determines whether or not we ignore missing
     * resources
     */
    protected BaseConfig(final String resource, final boolean required) {
        Assert.exists(resource, String.class);

        m_params = new ArrayList();
        m_values = new HashMap();

        final Properties props = new Properties();

        final InputStream in = getClass().getResourceAsStream(resource);

        if (in == null) {
            if (required) {
                throw new ConfigurationError
                    ("Resource " + resource + " not found; it is required");
            } else {
                if (s_log.isInfoEnabled()) {
                    s_log.info(resource + " was not found; using an empty " +
                               "property record");
                }
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Loading configuration values from " +
                            resource + " on the class path");
            }

            try {
                props.load(in);
            } catch (IOException ioe) {
                throw new UncheckedWrapperException(ioe);
            }
        }

        m_store = new JavaPropertyStore(props);
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
        this(resource, false);
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
        if (s_log.isDebugEnabled()) {
            s_log.debug("Initializing value for " + param);
        }

        Assert.exists(param, Parameter.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Unmarshaling parameter from " + m_store);
        }

        final ParameterValue value = param.unmarshal(m_store);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        return set(param, value);
    }

    private Object set(final Parameter param, final ParameterValue value) {
        Assert.exists(param, Parameter.class);
        Assert.exists(value, ParameterValue.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Validating parameter value " + value);
        }

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigurationError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        final Object result = value.getValue();

        synchronized (m_values) {
            m_values.put(param, result);
        }

        return result;
    }

    /**
     * Gets the value of <code>param</code>.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to retrieve; it cannot be null
     */
    protected final Object get(final Parameter param) {
        Assert.exists(param, Parameter.class);

        synchronized (m_values) {
            return m_values.get(param);
        }
    }

    /**
     * Sets the value of <code>param</code> to <code>value</code>.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to set; it cannot be null
     * @param value The new value of <code>param</code>; it can be
     * null
     */
    protected final void set(final Parameter param, final Object value) {
        Assert.exists(param, Parameter.class);

        set(param, new ParameterValue(value, new ArrayList()));
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + parameterStore.toString()
     */
    public String toString() {
        return super.toString() + ":" + m_store;
    }
}
