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
 * @see com.arsdigita.util.parameter.ParameterLoader
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#1 $
 */
public abstract class ParameterRecord {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/23 01:57:55 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    private final String m_name;
    private final ArrayList m_params;
    private final HashMap m_values;
    private final Properties m_info;

    protected ParameterRecord(final String name) {
        m_name = name;
        m_params = new ArrayList();
        m_values = new HashMap();
        m_info = new Properties();

        ParameterPrinter.register(this);
    }

    protected final void register(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering " + param + " on " + this);
        }

        if (Assert.isEnabled()) {
            Assert.truth(!m_params.contains(param),
                         param + " is already registered");
        }

        m_params.add(param);
    }

    public final void load(final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading all registered params on " + this);
        }

        Assert.exists(loader, ParameterLoader.class);

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            load((Parameter) params.next(), loader);
        }
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

        set(param, new ParameterValue(value));
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

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            param.setInfo(new Info(param));
        }
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + name
     */
    public String toString() {
        return super.toString() + ":" + m_name;
    }

    //
    // Private classes and methods
    //

    private Object load(final Parameter param, final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading " + param + " from " + loader);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(loader, ParameterLoader.class);
            Assert.truth(m_params.contains(param),
                         param + " has not been registered");
        }

        final ParameterValue value = loader.load(param);

        if (value == null) {
            throw new IllegalArgumentException("XXX");
        }

        if (!value.getErrors().isEmpty()) {
            throw new IllegalArgumentException
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
                         param + " has not been registered");
        }

        param.validate(value);

        if (!value.getErrors().isEmpty()) {
            throw new ConfigError
                ("Parameter " + param.getName() + ": " +
                 value.getErrors().toString());
        }

        final Object result = value.getObject();

        synchronized (m_values) {
            m_values.put(param, result);
        }

        return result;
    }

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

    void writeXML(final PrintWriter out) {
        Assert.exists(out, PrintWriter.class);

        out.write("<record>");
        field(out, "name", m_name);

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            out.write("<parameter>");

            field(out, "name", param.getName());

            if (param.isRequired()) {
                out.write("<required/>");
            }

            final Object defaalt = param.getDefaultValue();

            if (defaalt != null) {
                if (defaalt instanceof Object[]) {
                    final Object[] elems = (Object[]) defaalt;
                    final StringBuffer buffer = new StringBuffer();

                    for (int i = 0; i < elems.length; i++) {
                        buffer.append(elems[i].toString());
                        buffer.append(", ");
                    }

                    final int len = buffer.length();

                    if (len > 2) {
                        field(out, "default", buffer.substring(0, len - 2));
                    }
                } else {
                    field(out, "default", defaalt.toString());
                }
            }

            final ParameterInfo info = param.getInfo();

            field(out, "title", info.getTitle());
            field(out, "purpose", info.getPurpose());
            field(out, "example", info.getExample());
            field(out, "format", info.getFormat());

            out.write("</parameter>");
        }

        out.write("</record>");
    }

    private void field(final PrintWriter out,
                       final String name,
                       final String value) {
        if (value != null) {
            out.write("<");
            out.write(name);
            out.write("><![CDATA[");
            out.write(value);
            out.write("]]></");
            out.write(name);
            out.write(">");
        }
    }
}
