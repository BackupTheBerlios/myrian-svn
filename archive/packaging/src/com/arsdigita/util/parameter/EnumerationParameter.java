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
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/EnumerationParameter.java#2 $
 */
public abstract class EnumerationParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/EnumerationParameter.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/27 12:11:05 $";

    private static final Logger s_log = Logger.getLogger
        (EnumerationParameter.class);

    private final String m_name;
    private final HashMap m_entries;
    private boolean m_required;

    public EnumerationParameter(final String name) {
        m_name = name;
        m_entries = new HashMap();
        m_required = false;
    }

    public void addEntry(final String name, final Object value) {
        m_entries.put(name, value);
    }

    // Default is true.
    public final boolean isRequired() {
        return m_required;
    }

    public void setRequired(final boolean required) {
        m_required = required;
    }

    public final String getName() {
        return m_name;
    }

    public ParameterValue unmarshal(final ParameterStore store) {
        final ParameterValue value = new ParameterValue();
        final String literal = store.read(this);

        if (literal != null) {
            if (m_entries.containsKey(literal)) {
                value.setValue(m_entries.get(literal));
            } else {
                value.addError("It must be one of the following: " +
                               m_entries.keySet());
            }
        }

        return value;
    }

    public void validate(final ParameterValue value) {
        if (isRequired() && value.getValue() == null) {
            value.addError("It cannot be null");
        }
    }
}
