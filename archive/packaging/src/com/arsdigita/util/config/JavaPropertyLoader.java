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
 * @deprecated Use {@link com.arsdigita.util.JavaPropertyReader}
 * instead
 */
public class JavaPropertyLoader extends JavaPropertyReader
        implements ParameterLoader {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyLoader.java#8 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/17 19:02:11 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyLoader.class);

    private final Properties m_props;

    /**
     * Constructs a parameter loader that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyLoader(final Properties props) {
        super(props);

        m_props = props;
    }

    public final ParameterValue load(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading " + param + " from " + this);
        }

        Assert.exists(param, Parameter.class);

        final String key = param.getName();

        if (m_props.containsKey(key)) {
            final ParameterValue value = new ParameterValue();

            value.setObject(param.read(this, value.getErrors()));

            return value;
        } else {
            return null;
        }
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
