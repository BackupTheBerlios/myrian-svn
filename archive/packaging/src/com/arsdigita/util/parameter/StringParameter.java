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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#5 $
 */
public class StringParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#5 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/02 12:40:15 $";

    private final String m_name;
    private final Class m_type;
    private final int m_multiplicity;
    private final Object m_default;

    static {
        Converters.set(String.class, new StringConverter());
    }

    protected StringParameter(final String name,
                              final int multiplicity,
                              final Object defaalt,
                              final Class type) {
        if (Assert.isEnabled()) {
            Assert.exists(name, String.class);
            Assert.exists(type, Class.class);
        }

        m_name = name;
        m_type = type;
        m_multiplicity = multiplicity;
        m_default = defaalt;
    }

    protected StringParameter(final String name,
                              final Class type) {
        this(name, Parameter.REQUIRED, null, type);
    }

    public StringParameter(final String name,
                           final int multiplicity,
                           final Object defaalt) {
        this(name, multiplicity, defaalt, String.class);
    }

    public StringParameter(final String name) {
        this(name, Parameter.REQUIRED, null);
    }

    public final boolean isRequired() {
        return m_multiplicity == Parameter.REQUIRED;
    }

    public final String getName() {
        return m_name;
    }

    public final ParameterValue unmarshal(final ParameterStore store) {
        final ParameterValue value = new ParameterValue();
        final String literal = store.read(this);

        if (literal == null) {
            value.setValue(m_default);
        } else {
            value.setValue(unmarshal(literal, value.getErrors()));
        }

        return value;
    }

    protected Object unmarshal(final String value, final List errors) {
        try {
            return Converters.convert(m_type, value);
        } catch (ConversionException ce) {
            errors.add(ce.getMessage());

            return null;
        }
    }

    public final void validate(final ParameterValue value) {
        final Object object = value.getValue();
        final List errors = value.getErrors();

        if (isRequired() && object == null) {
            errors.add("The value must not be null");
        } else {
            validate(object, errors);
        }
    }

    protected void validate(final Object value, final List errors) {
        // Nothing by default
    }
}
