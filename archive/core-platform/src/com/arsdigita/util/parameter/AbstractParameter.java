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

import com.arsdigita.util.Assert;
import org.apache.commons.beanutils.ConversionException;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/AbstractParameter.java#3 $
 */
public abstract class AbstractParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/AbstractParameter.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/28 18:36:21 $";

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
        }

        m_name = name;
        m_type = type;
        m_multiplicity = multiplicity;
        m_default = defaalt;
    }

    protected AbstractParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
        this(name, multiplicity, defaalt, null);
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

    public final Object read(final ParameterReader reader,
                             final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(reader, ParameterReader.class);
            Assert.exists(errors, ErrorList.class);
        }

        return doRead(reader, errors);
    }

    protected Object doRead(final ParameterReader reader,
                            final ErrorList errors) {
        final String string = reader.read(this, errors);

        if (string == null) {
            return null;
        } else {
            return unmarshal(string, errors);
        }
    }

    // value != null
    protected Object unmarshal(final String value, final ErrorList errors) {
        Assert.exists(value, String.class);

        try {
            return Converters.convert(m_type, value);
        } catch (ConversionException ce) {
            errors.add(new ParameterError(this, ce));
            return null;
        }
    }

    public final void validate(final Object value, final ErrorList errors) {
        Assert.exists(errors, ErrorList.class);

        if (value == null) {
            // If the value is null, validation stops here.

            if (isRequired()) {
                final ParameterError error = new ParameterError
                    (this, "The value must not be null");
                errors.add(error);
            }
        } else {
            // Always do further validation for non-null values.

            doValidate(value, errors);
        }
    }

    // value != null
    protected void doValidate(final Object value, final ErrorList errors) {
        Assert.exists(value, Object.class);

        // Nothing
    }

    public final void write(final ParameterWriter writer, final Object value) {
        Assert.exists(writer);

        // XXX what to do about nulls here?

        doWrite(writer, value);
    }

    protected void doWrite(final ParameterWriter writer, final Object value) {
        writer.write(this, marshal(value));
    }

    protected String marshal(final Object value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    public String toString() {
        return super.toString() + "," + getName() + "," + isRequired();
    }
}
