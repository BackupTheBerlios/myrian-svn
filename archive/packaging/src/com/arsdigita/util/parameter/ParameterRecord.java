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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#12 $
 */
public abstract class ParameterRecord extends AbstractParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#12 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/17 14:30:44 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    private final String m_name;
    private final ArrayList m_registered;
    private final HashSet m_loaded;
    private final Map m_values;
    private final Properties m_info;

    protected ParameterRecord(final String name) {
        super(name);

        m_name = name;
        m_registered = new ArrayList();
        m_loaded = new HashSet();
        m_values = Collections.synchronizedMap(new HashMap());
        m_info = new Properties();
    }

    public final void load(final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading all registered params on " + this);
        }

        Assert.exists(loader, ParameterLoader.class);

        load((ParameterReader) loader);
    }

    // Does not param.check.
    protected final ParameterValue getValue(final Parameter param) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting value of " + param);
        }

        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
        }

        final ParameterValue value = new ParameterValue();

        value.setObject(get(param));

        return value;
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
     * @return super.toString() + "," + name
     */
    public String toString() {
        return super.toString() + "," + m_name;
    }

    //
    // Private classes and methods
    //

    void writeXML(final PrintWriter out) {
        Assert.exists(out, PrintWriter.class);

//         out.write("<record>");
//         field(out, "name", m_name);

//         final Iterator params = m_registered.iterator();

//         while (params.hasNext()) {
//             final Parameter param = (Parameter) params.next();

//             out.write("<parameter>");

//             field(out, "name", param.getName());

//             if (param.isRequired()) {
//                 out.write("<required/>");
//             }

//             final ParameterInfo info = param.getInfo();

//             if (info != null) {
//                 field(out, "title", info.getTitle());
//                 field(out, "purpose", info.getPurpose());
//                 field(out, "example", info.getExample());
//                 field(out, "format", info.getFormat());
//             }

//             out.write("</parameter>");
//         }

//         out.write("</record>");
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
