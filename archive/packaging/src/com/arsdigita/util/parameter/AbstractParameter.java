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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameter.java#4 $
 */
public abstract class AbstractParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameter.java#4 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/23 11:43:44 $";

    private final String m_name;
    private final Class m_type;
    private final int m_multiplicity;
    private final Object m_default;
    private ParameterInfo m_info;

    protected AbstractParameter(final String name,
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

    protected AbstractParameter(final String name,
                                final Class type) {
        this(name, Parameter.REQUIRED, null, type);
    }

    public final boolean isRequired() {
        return m_multiplicity == Parameter.REQUIRED;
    }

    public final String getName() {
        return m_name;
    }

    // Not final.  This is an extension point.
    public Object getDefaultValue() {
        return m_default;
    }

    public final ParameterInfo getInfo() {
        return m_info;
    }

    public final void setInfo(final ParameterInfo info) {
        m_info = info;
    }

    //
    // Lifecycle events
    //

    public final void unmarshal(final ParameterValue value) {
        Assert.exists(value, ParameterValue.class);

        final String string = value.getString();

        if (string == null) {
            value.setObject(getDefaultValue());
        } else {
            final List errors = value.getErrors();

            final Object result = unmarshal(string, errors);

            value.setObject(result);
        }
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
        final Object object = value.getObject();
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
