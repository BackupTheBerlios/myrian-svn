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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyStore.java#1 $
 */
public class JavaPropertyStore implements ParameterStore {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyStore.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/02 14:11:43 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyStore.class);

    private final Properties m_props;


    public JavaPropertyStore(final Properties props) {
        Assert.exists(props, Properties.class);

        m_props = props;
    }

    public String read(final Parameter param) {
        final String name = param.getName();
        final String value = System.getProperty(name);

        if (value == null) {
            return m_props.getProperty(name);
        } else {
            return value;
        }
    }

    public void write(final Parameter param, final String value) {
        final String name = param.getName();

        // If it was set as a system property before, re-set it.

        if (System.getProperty(name) != null) {
            System.setProperty(name, value);
        }

        m_props.setProperty(name, value);
    }
}
