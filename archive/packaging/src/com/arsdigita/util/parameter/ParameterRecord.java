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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#11 $
 */
public abstract class ParameterRecord {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#11 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/09 10:25:57 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    private final String m_name;
    private final ArrayList m_registered;
    private final HashSet m_loaded;
    private final Map m_values;
    private final Properties m_info;

    protected ParameterRecord(final String name) {
        m_name = name;
        m_registered = new ArrayList();
        m_loaded = new HashSet();
        m_values = Collections.synchronizedMap(new HashMap());
        m_info = new Properties();
    }

    public final Parameter[] getParameters() {
        Parameter[] result = new Parameter[m_registered.size()];
        return (Parameter[]) m_registered.toArray(result);
    }

    protected final void register(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering " + param.getName() +
                        " (" + param + ") on " + this);
        }

        if (Assert.isEnabled()) {
            Assert.truth(!m_registered.contains(param),
                         param + " is already registered");
        }

        m_registered.add(param);
    }

    public final void load(final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading all registered params on " + this);
        }

        Assert.exists(loader, ParameterLoader.class);

        final Iterator params = m_registered.iterator();

        while (params.hasNext()) {
            load((Parameter) params.next(), loader);
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
            m_info.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        final Iterator params = m_registered.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            param.setInfo(new Info(param));
        }
    }

    /**
     * Gets the value of <code>param</code>.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to retrieve; it cannot be null
     */
    protected final Object get(final Parameter param) {
        if (Assert.isEnabled()) {
            Assert.truth(m_registered.contains(param),
                         param + " has not been registered");
            Assert.truth(m_loaded.contains(param),
                         param + " has not been loaded");
        }

        final ParameterValue value = getValue(param);

        if (value == null) {
            return param.getDefaultValue();
        } else {
            param.check(value);

            param.validate(value);

            param.check(value);

            return value.getObject();
        }
    }

    // Does not param.check.
    protected final ParameterValue getValue(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting value of " + param.getName() +
                        " (" + param + ")");
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
        }

        return (ParameterValue) m_values.get(param);
    }

    /**
     * Sets the value of <code>param</code> to <code>value</code>.
     *
     * @param param The named <code>Parameter</code> whose value you
     * wish to set; it cannot be null
     * @param value The new value of <code>param</code>; it can be
     * null
     */
    protected final void set(final Parameter param, final Object object) {
        Assert.exists(param, Parameter.class);

        final ParameterValue value = new ParameterValue();

        value.setObject(object);

        param.validate(value);

        param.check(value);

        set(param, value);
    }

    protected final void setValue(final Parameter param,
                                  final ParameterValue value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting " + param.getName() +
                        " (" + param + ") to " + value);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.truth(m_registered.contains(param),
                         param + " has not been registered");
        }

        m_values.put(param, value);
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

    private void load(final Parameter param, final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading " + param.getName() +
                        " (" + param + ") from " + loader);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(loader, ParameterLoader.class);
            Assert.truth(m_registered.contains(param),
                         param + " has not been registered");

            m_loaded.add(param);
        }

        final ParameterValue value = loader.load(param);

        if (value != null) {
            param.check(value);
        }

        setValue(param, value);
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

        final Iterator params = m_registered.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            out.write("<parameter>");

            field(out, "name", param.getName());

            if (param.isRequired()) {
                out.write("<required/>");
            }

            final ParameterInfo info = param.getInfo();

            if (info != null) {
                field(out, "title", info.getTitle());
                field(out, "purpose", info.getPurpose());
                field(out, "example", info.getExample());
                field(out, "format", info.getFormat());
            }

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
