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

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An implementation of <code>ParameterReader</code> that uses
 * standard Java properties to retrieve values.
 *
 * @see com.arsdigita.util.parameter.ParameterReader
 * @see JavaPropertyWriter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/JavaPropertyReader.java#3 $
 */
public class JavaPropertyReader implements ParameterReader {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/JavaPropertyReader.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:29:19 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyReader.class);

    private final Properties m_props;

    /**
     * Constructs a parameter reader that uses <code>props</code>.
     *
     * @param props The <code>Properties</code> object that stores
     * property values; it cannot be null
     */
    public JavaPropertyReader(final Properties props) {
        Assert.exists(props, Properties.class);

        m_props = props;
    }

    /**
     * Loads the internal <code>Properties</code> object using
     * <code>in</code>.
     *
     * @param in The <code>InputStream</code> that has the source
     * properties; it cannot be null
     */
    public final void load(final InputStream in) {
        Assert.exists(in, InputStream.class);

        try {
            m_props.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    /**
     * Reads a <code>String</code> value back for a
     * <code>param</code>.
     *
     * @param param The <code>Parameter</code> whose value is
     * requested; it cannot be null
     * @param errors An <code>ErrorList</code> to trap any errors
     * encountered when reading; it cannot be null
     * @return The <code>String</code> value for <code>param</code>;
     * it can be null
     */
    public final String read(final Parameter param, final ErrorList errors) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Reading " + param + " from " + m_props);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(errors, ErrorList.class);
        }

        return m_props.getProperty(param.getName());
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
