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
 * An implementation of <code>ParameterStore</code> that uses standard
 * Java properties to store and retrieve values.
 *
 * @see com.arsdigita.util.parameter.ParameterStore
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyStore.java#3 $
 */
public final class JavaPropertyStore implements ParameterStore {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyStore.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/17 11:49:03 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyStore.class);

    private final Properties m_props;

    /**
     * Constructs a parameter store that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyStore(final Properties props) {
        Assert.exists(props, Properties.class);

        m_props = props;
    }

    /**
     * Reads a marshaled <code>String</code> value from the Java
     * properties object for parameter <code>param</code>.
     *
     * @param param The named <code>Parameter</code> whose raw value
     * you wish to retrieve; it cannot be null
     * @return The marshaled <code>String</code> value of
     * <code>param</code> from the Java property store
     */
    public String read(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Reading " + param + " from " + this);
        }

        Assert.exists(param, Parameter.class);

        return m_props.getProperty(param.getName());
    }

    /**
     * Writes the marshaled <code>value</code> to the Java property
     * object.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to set; it cannot be null
     * @param value The marshaled <code>String</code> value to set
     * <code>param</code> to; it can be null
     */
    public void write(final Parameter param, final String value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Writing " + param + " with value "
                        + value + " to " + this);
        }

        Assert.exists(param, Parameter.class);

        m_props.setProperty(param.getName(), value);
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + properties.size()
     */
    public String toString() {
        return super.toString() + ":" + m_props.size();
    }
}
