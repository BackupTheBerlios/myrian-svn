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

package com.arsdigita.util;

import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An implementation of <code>ParameterWriter</code> that uses
 * standard Java properties to store values.
 *
 * @see com.arsdigita.util.parameter.ParameterWriter
 * @see JavaPropertyReader
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/JavaPropertyWriter.java#1 $
 */
public class JavaPropertyWriter implements ParameterWriter {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/JavaPropertyWriter.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyWriter.class);

    private static final String s_header =
        " Generated by " + JavaPropertyWriter.class.getName();

    private final Properties m_props;

    /**
     * Constructs a parameter writer that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyWriter(final Properties props) {
        Assert.exists(props, Properties.class);

        m_props = props;
    }

    /**
     * Tells the internal property object to store its values to
     * <code>out</code>.
     *
     * @param out The <code>OutputStream</code> to send the saved
     * parameters to; it cannot be null
     */
    public final void store(final OutputStream out) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Storing " + this);
        }

        Assert.exists(out, OutputStream.class);

        try {
            m_props.store(out, s_header);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    /**
     * Writes a <code>String</code> value back for a
     * <code>param</code>.
     *
     * @param param The <code>Parameter</code> whose value is
     * to be written; it cannot be null
     * @param value The <code>String</code> value to write out; it can
     * be null
     */
    public final void write(final Parameter param, final String value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Writing " + param + " with value " + value);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
        }

        // XXX: Properties objects blow up when you try to put null
        // values in them. This null check fixes it for now, but it
        // doesn't let us explicitly write out a null value if that's
        // what we actually want to store. I.e. our property store
        // doesn't know the difference between a parameter being
        // unspecified and a parameter being explicitly set to null.
        if (value != null) {
            m_props.setProperty(param.getName(), value);
        }
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + "," + properties.size()
     */
    public String toString() {
        return super.toString() + "," + m_props.size();
    }
}
