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
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#3 $
 */
public class StringParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/StringParameter.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/27 12:11:05 $";

    private final String m_name;
    private final Class m_type;

    private boolean m_required;

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
        m_required = true;
    }

    public StringParameter(final String name) {
        this(name, String.class);
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
            try {
                value.setValue(unmarshal(literal));
            } catch (ConversionException ce) {
                value.addError(ce.getMessage());
            }
        }

        return value;
    }

    public void validate(final ParameterValue value) {
        if (isRequired() && value.getValue() == null) {
            value.addError("It cannot be null");
        }
    }

    protected Object unmarshal(final String string) {
        return Converters.convert(m_type, string);
    }
}
