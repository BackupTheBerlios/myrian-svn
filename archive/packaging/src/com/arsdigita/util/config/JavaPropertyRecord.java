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
 * @see com.arsdigita.util.parameter.ParameterStore
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyRecord.java#2 $
 */
public class JavaPropertyRecord extends ConfigRecord {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyRecord.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/19 02:38:52 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyRecord.class);

    private final Properties m_props;
    private final JavaPropertyStore m_store;
    private final Properties m_infos;

    protected JavaPropertyRecord(final String name,
                                 final Properties props) {
        super(name);

        m_props = props;
        m_store = new JavaPropertyStore(props);
        m_infos = new Properties();
    }

    protected final void load(final InputStream in) {
        try {
            m_props.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    /**
     * Loads source data for <code>ParameterInfo</code> objects from
     * the file <code>parameter.info</code> next to
     * <code>this.getClass()</code>.
     */
    protected final void loadInfo() {
        final String name = getClass().getName().replace('.', '/');
        final InputStream in = getClass().getClassLoader
            ().getResourceAsStream(name + "_parameter.properties");

        Assert.exists(in, InputStream.class);

        try {
            m_infos.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    protected final Object initialize(final Parameter param) {
        final Object value = super.initialize(param, m_store);

        setInfo(param, new Info(param));

        return value;
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + parameterStore
     */
    public String toString() {
        return super.toString() + ":" + m_store;
    }

    //
    // Private classes and methods
    //

    private class Info implements ParameterInfo {
        private final String m_name;

        Info(final Parameter param) {
            m_name = param.getName();
        }

        public final String getTitle() {
            return m_infos.getProperty(m_name + ".title");
        }

        public final String getPurpose() {
            return m_infos.getProperty(m_name + ".purpose");
        }

        public final String getExample() {
            return m_infos.getProperty(m_name + ".example");
        }

        public final String getFormat() {
            return m_infos.getProperty(m_name + ".format");
        }
    }
}
