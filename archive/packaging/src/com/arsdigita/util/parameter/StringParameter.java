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
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#2 $
 */
public class StringParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#2 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/26 20:38:18 $";

    private final String m_name;
    private final Class m_type;

    private boolean m_isRequired;
    private Object m_value;
    private Object m_defaultValue;

    static {
        Converters.set(String.class, new StringConverter());
    }

    protected StringParameter(final String name, final Class type) {
        if (Assert.isEnabled()) {
            Assert.exists(name, String.class);
            Assert.exists(type, Class.class);
        }

        m_name = name;
        m_type = type;
        m_isRequired = true;
    }

    public StringParameter(final String name) {
        this(name, String.class);
    }

    // Default is true.
    public final boolean isRequired() {
        return m_isRequired;
    }

    public void setRequired(final boolean isRequired) {
        m_isRequired = isRequired;
    }

    public final String getName() {
        return m_name;
    }

    public Object getValue(final ParameterStore store) {
        synchronized (this) {
            if (m_value == null) {
                final String value = store.read(this);

                if (value != null) {
                    m_value = unmarshal(value);
                }
            }
        }

        if (m_value == null) {
            return m_defaultValue;
        } else {
            return m_value;
        }
    }

    public void setDefaultValue(final Object defaultValue) {
        m_defaultValue = defaultValue;
    }

    public List validate(final ParameterStore store) {
        final String value = store.read(this);
        final ArrayList errors = new ArrayList();

        if (isRequired() && m_defaultValue == null && value == null) {
            addError(errors, "It cannot be null");
        }

        if (value != null) {
            try {
                unmarshal(value);
            } catch (ConversionException ce) {
                addError(errors, ce.getMessage());
            }
        }

        return errors;
    }

    protected final void addError(final List errors, final String message) {
        errors.add("The value of parameter " + getName() + " is invalid: " + message);
    }

    protected Object unmarshal(final String string) {
        return Converters.convert(m_type, string);
    }
}
