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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyRecord.java#1 $
 */
public class JavaPropertyRecord extends ConfigRecord {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/config/JavaPropertyRecord.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/16 13:41:28 $";

    private static final Logger s_log = Logger.getLogger
        (JavaPropertyRecord.class);

    private final Properties m_props;
    private final JavaPropertyStore m_store;

    protected JavaPropertyRecord(final String name, final Properties props) {
        super(name);

        m_props = props;
        m_store = new JavaPropertyStore(props);
    }

    protected final void load(final InputStream in) {
        try {
            m_props.load(in);
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    protected final Object initialize(final Parameter param) {
        return super.initialize(param, m_store);
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return super.toString() + ":" + parameterStore
     */
    public String toString() {
        return super.toString() + ":" + m_store;
    }
}
