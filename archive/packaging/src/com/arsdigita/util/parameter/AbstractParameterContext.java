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

package com.arsdigita.util.parameter;

import com.arsdigita.util.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameterContext.java#5 $
 */
public abstract class AbstractParameterContext implements ParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameterContext.java#5 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/21 21:30:18 $";

    private static final Logger s_log = Logger.getLogger
        (AbstractParameterContext.class);

    private final MapParameter m_param;
    private final HashMap m_map;
    private final Properties m_info;

    public AbstractParameterContext() {
        m_param = new MapParameter("root");
        m_map = new HashMap();
        m_info = new Properties();
    }

    public final void register(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering " + param + " on " + this);
        }

        if (Assert.isEnabled()) {
            Assert.truth(!m_param.contains(param),
                         param + " is already registered");
        }

        m_param.add(param);
    }

    // XXX change this?
    public final Parameter[] getParameters() {
        final ArrayList list = new ArrayList();
        final Iterator params = m_param.iterator();

        while (params.hasNext()) {
            list.add(params.next());
        }

        return (Parameter[]) list.toArray(new Parameter[list.size()]);
    }

    /**
     * Gets the value of <code>param</code>.  If the loaded value is
     * null, <code>param.getDefaultValue()</code> is returned.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to retrieve; it cannot be null
     */
    public Object get(final Parameter param) {
        return get(param, param.getDefaultValue());
    }

    public Object get(final Parameter param, final Object dephault) {
        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.truth(m_param.contains(param),
                         param + " has not been registered");
        }

        // XXX check for is loaded?

        final Object value = m_map.get(param);

        if (value == null) {
            return dephault;
        } else {
            return value;
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
    public void set(final Parameter param, final Object value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting " + param + " to " + value);
        }

        Assert.exists(param, Parameter.class);

        m_map.put(param, value);
    }

    public final ErrorList load(final ParameterReader reader) {
        final ErrorList errors = new ErrorList();

        load(reader, errors);

        return errors;
    }

    public final void load(final ParameterReader reader,
                           final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(reader, ParameterReader.class);
            Assert.exists(errors, ErrorList.class);
        }

        m_map.putAll((Map) m_param.read(reader, errors));
    }

    public final ErrorList validate() {
        final ErrorList errors = new ErrorList();

        m_param.validate(m_map, errors);

        return errors;
    }

    public final void validate(final ErrorList errors) {
        Assert.exists(errors, ErrorList.class);

        m_param.validate(m_map, errors);
    }

    public final void save(ParameterWriter writer) {
        m_param.write(writer, m_map);
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
            m_info.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        final Iterator params = m_param.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            param.setInfo(new Info(param));
        }
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
            return m_info.getProperty(m_name + ".title");
        }

        public final String getPurpose() {
            return m_info.getProperty(m_name + ".purpose");
        }

        public final String getExample() {
            return m_info.getProperty(m_name + ".example");
        }

        public final String getFormat() {
            return m_info.getProperty(m_name + ".format");
        }
    }
}
