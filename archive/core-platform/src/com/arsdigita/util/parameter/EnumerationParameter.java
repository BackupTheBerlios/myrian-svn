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

import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * A parameter that maps keys to values and, given a key, marshals or
 * unmarshals to the corresponding value.
 *
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/EnumerationParameter.java#4 $
 */
public class EnumerationParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/EnumerationParameter.java#4 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:56:08 $";

    private static final Logger s_log = Logger.getLogger
        (EnumerationParameter.class);

    private final HashMap m_entries;
    private final HashMap m_reverse;

    public EnumerationParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
        super(name, multiplicity, defaalt);

        m_entries = new HashMap();
        m_reverse = new HashMap();
    }

    public EnumerationParameter(final String name) {
        this(name, Parameter.REQUIRED, null);
    }

    public final void put(final String name, final Object value) {
        if (m_entries.containsKey(name)) {
            throw new IllegalArgumentException
                ("name already has a value: " + name);
        }
        if (m_reverse.containsKey(value)) {
            throw new IllegalArgumentException
                ("value already has a name: " + value);
        }
        m_entries.put(name, value);
        m_reverse.put(value, name);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        if (m_entries.containsKey(value)) {
            return m_entries.get(value);
        } else {
            final ParameterError error = new ParameterError
                (this, "The value must be one of " + m_entries.keySet());

            errors.add(error);

            return null;
        }
    }

    protected String marshal(Object value) {
        return (String) m_reverse.get(value);
    }

}
