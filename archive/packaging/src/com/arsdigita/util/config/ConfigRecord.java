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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/ConfigRecord.java#1 $
 */
public class ConfigRecord {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/ConfigRecord.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/16 13:41:28 $";

    private static final Logger s_log = Logger.getLogger
        (ConfigRecord.class);

    private final String m_name;
    private final ArrayList m_params;
    private final HashMap m_values;

    protected ConfigRecord(final String name) {
        m_name = name;
        m_params = new ArrayList();
        m_values = new HashMap();
    }

    /**
     * Initializes a named parameter (represented by
     * <code>param</code> by unmarshaling and validating it.  If any
     * errors are encountered during the unmarshal or validate steps,
     * a <code>ConfigError</code> listing the error messages is
     * thrown.
     *
     * @throws com.arsdigita.util.config.ConfigError
     * @param param The named <code>Parameter</code> you wish to
     * fetch; it cannot be null
     * @return The unmarshaled value of <code>param</code>; it may be
     * null
     */
    protected final Object initialize(final Parameter param,
                                      final ParameterStore store) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Initializing value of " + param + " from " +
                        store);
        }

        Assert.exists(param, Parameter.class);
        Assert.exists(store, ParameterStore.class);

        m_params.add(param);

        final ParameterValue value = param.unmarshal(store);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        return set(param, value);
    }

    private Object set(final Parameter param, final ParameterValue value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting " + param + " to " + value);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(value, ParameterValue.class);
            Assert.truth(m_params.contains(param),
                         param + " has not been initialized");
        }

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigError
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
     * @return super.toString() + ":" + name
     */
    public String toString() {
        return super.toString() + ":" + m_name;
    }
}
