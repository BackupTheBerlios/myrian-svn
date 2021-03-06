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

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * A base implementation of the <code>ParameterContext</code>
 * interface.
 *
 * @see com.arsdigita.util.parameter.ParameterContext
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/AbstractParameterContext.java#1 $
 */
public abstract class AbstractParameterContext implements ParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/AbstractParameterContext.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private static final Logger s_log = Logger.getLogger
        (AbstractParameterContext.class);

    private final MapParameter m_param;
    private final HashMap m_map;
    private final Properties m_info;

    /**
     * Constructs a parameter context.
     */
    public AbstractParameterContext() {
        m_param = new MapParameter("root");
        m_map = new HashMap();
        m_info = new Properties();
    }

    /**
     * Registers <code>param</code> to the context.
     *
     * @param param The <code>Parameter</code> being registered; it
     * cannot be null
     */
    public final void register(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Registering " + param + " on " + this);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.truth(!m_param.contains(param),
                         param + " is already registered");
        }

        m_param.add(param);
    }

    /**
     * @see ParameterContext#getParameters()
     */
    public final Parameter[] getParameters() {
        final ArrayList list = new ArrayList();
        final Iterator params = m_param.iterator();

        while (params.hasNext()) {
            list.add(params.next());
        }

        return (Parameter[]) list.toArray(new Parameter[list.size()]);
    }

    /**
     * @see ParameterContext#get(Parameter)
     */
    public Object get(final Parameter param) {
        return get(param, param.getDefaultValue());
    }

    /**
     * @see ParameterContext#get(Parameter,Object)
     */
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
     * @see ParameterContext#get(Parameter,Object)
     */
    public void set(final Parameter param, final Object value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting " + param + " to " + value);
        }

        Assert.exists(param, Parameter.class);

        m_map.put(param, value);
    }

    /**
     * Reads and unmarshals all values associated with the registered
     * parameters from <code>reader</code>.  Any errors are returned.
     *
     * @param reader The <code>ParameterReader</code> from which to
     * fetch the values; it cannot be null
     * @return An <code>ErrorList</code> containing any errors
     * encountered while loading; it cannot be null
     */
    public final ErrorList load(final ParameterReader reader) {
        final ErrorList errors = new ErrorList();

        load(reader, errors);

        return errors;
    }

    /**
     * @see ParameterContext#load(ParameterReader,ErrorList)
     */
    public final void load(final ParameterReader reader,
                           final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(reader, ParameterReader.class);
            Assert.exists(errors, ErrorList.class);
        }

        m_map.putAll((Map) m_param.read(reader, errors));
    }

    /**
     * Validates all values associated with the registered parameters.
     * Any errors encountered are returned.
     *
     * @return An <code>ErrorList</code> containing validation errors;
     * it cannot be null
     */
    public final ErrorList validate() {
        final ErrorList errors = new ErrorList();

        m_param.validate(m_map, errors);

        return errors;
    }

    /**
     * @see ParameterContext#validate(ErrorList)
     */
    public final void validate(final ErrorList errors) {
        Assert.exists(errors, ErrorList.class);

        m_param.validate(m_map, errors);
    }

    /**
     * @see ParameterContext#save(ParameterWriter)
     */
    public final void save(ParameterWriter writer) {
        m_param.write(writer, m_map);
    }

    /**
     * Loads source data for <code>ParameterInfo</code> objects from
     * the file <code>YourClass_parameter.properties</code> next to
     * <code>YourClass.class</code>.
     *
     * <code>YourClass_parameter.properties</code>:
     *
     * <blockquote><pre>
     * yourforum.notification_enabled.title=Flag to enable forum notifications
     * yourforum.notification_enabled.purpose=Enables or disables forum notifications
     * yourforum.notification_enabled.example=true
     * yourforum.notifiaction_enabled.format=true|false
     * </pre></blockquote>
     *
     * @see ParameterInfo
     */
    protected final void loadInfo() {
        final InputStream in = findInfo(getClass());

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

    private static InputStream findInfo(final Class klass) {
        final List files = new LinkedList();
        InputStream in = findInfo(klass, files);
        if ( in == null ) {
            throw new IllegalStateException
                ("Could not find any of the following files: " + files);
        }
        return in;
    }

    private static InputStream findInfo(final Class klass, final List files) {
        if (klass == null) { return null; }
        final String name =
            klass.getName().replace('.', '/') + "_parameter.properties";
        files.add(name);
        if ( klass.getClassLoader() == null ) {
            return null;
        }
        final InputStream in = klass.getClassLoader().getResourceAsStream(name);

        if (in == null) {
            return findInfo(klass.getSuperclass(), files);
        } else {
            return in;
        }
    }
}
