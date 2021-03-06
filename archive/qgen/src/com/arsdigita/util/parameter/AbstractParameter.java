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
 * A base implementation of the <code>Parameter</code> interface.  It
 * offers subclasses use of the Apache BeanUtils framework, should
 * they opt to use it.
 *
 * Methods of the form <code>doXXX</code> are extension points for
 * subclasses.  The <code>isRequired()</code> and
 * <code>getDefaultValue()</code> methods may also be overriden.
 *
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/AbstractParameter.java#1 $
 */
public abstract class AbstractParameter implements Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/AbstractParameter.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private final String m_name;
    private final Class m_type;
    private final int m_multiplicity;
    private final Object m_default;
    private ParameterInfo m_info;

    /**
     * Constructs a new parameter with the default value
     * <code>defaalt</code> and using the beanutils converter
     * registered for <code>type</code>.
     *
     * @param name The name of the parameter; it cannot be null
     * @param multiplicity The multiplicity type of the parameter
     * @param defaalt The default value to use if the value is unset
     * or is null
     * @param type The <code>Class</code> whose beanutils converter
     * will be used to unmarshal literal values
     */
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

    /**
     * Constructs a new parameter with the default value
     * <code>defaalt</code>.
     *
     * @param name The name of the parameter; it cannot be null
     * @param multiplicity The multiplicity type of the parameter
     * @param defaalt The default value to use if the value is unset
     * or is null
     */
    protected AbstractParameter(final String name,
                                final int multiplicity,
                                final Object defaalt) {
        // XXX Get rid of this constructor?
        this(name, multiplicity, defaalt, null);
    }

    /**
     * Constructs a new parameter using the beanutils converter for
     * type <code>type</code>.  By default, the parameter is required
     * and has no default.
     *
     * @param name The name of the parameter; it cannot be null
     * @param type The <code>Class</code> whose beanutils converter
     * will be used to unmarshal literal values
     */
    protected AbstractParameter(final String name,
                                final Class type) {
        this(name, Parameter.REQUIRED, null, type);
    }

    /**
     * Parameter users may override this method to make the
     * multiplicity of the parameter dependent on the multiplicity of
     * related parameters.
     *
     * @see Parameter#isRequired()
     */
    public boolean isRequired() {
        return m_multiplicity == Parameter.REQUIRED;
    }

    /**
     * @see Parameter#getName()
     */
    public final String getName() {
        return m_name;
    }

    /**
     * Parameter users may override this method to achieve dynamic
     * defaulting.
     *
     * @see Parameter#getDefaultValue()
     */
    public Object getDefaultValue() {
        return m_default;
    }

    /**
     * @see Parameter#getInfo()
     */
    public final ParameterInfo getInfo() {
        return m_info;
    }

    /**
     * @see Parameter#setInfo(ParameterInfo)
     */
    public final void setInfo(final ParameterInfo info) {
        m_info = info;
    }

    //
    // Lifecycle events
    //

    /**
     * Calls {@link #doRead(ParameterReader,ErrorList)}.
     *
     * @see Parameter#read(ParameterReader,ErrorList)
     */
    public final Object read(final ParameterReader reader,
                             final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(reader, ParameterReader.class);
            Assert.exists(errors, ErrorList.class);
        }

        return doRead(reader, errors);
    }

    /**
     * Reads the value of the parameter from <code>reader</code>,
     * unmarshals it, and returns it.  If any errors are encountered,
     * they are added to <code>errors</code>.
     *
     * If the literal string value from <code>reader</code> is not
     * null, this method delegates to {@link
     * #unmarshal(String,ErrorList)}.
     *
     * This implementation is suited to a parameter with a singular
     * scalar value.  Subclasses that are compound parameters should
     * override this method to delegate to child parameters.
     *
     * @param reader The <code>ParameterReader</code> that will supply
     * the literal stored value for this parameter; it cannot be null
     * @param errors The <code>ErrorList</code> that will trap any
     * errors encountered; it cannot be null
     */
    protected Object doRead(final ParameterReader reader,
                            final ErrorList errors) {
        final String string = reader.read(this, errors);

        if (string == null) {
            return null;
        } else {
            return unmarshal(string, errors);
        }
    }

    /**
     * Converts a literal <code>String</code> value,
     * <code>value</code>, to a Java object, which is returned.
     *
     * @param value The <code>String</code> value to convert from; it
     * cannot be null
     * @param errors An <code>ErrorList</code> that holds any errors
     * encountered during unmarshaling; it cannot be null
     */
    protected Object unmarshal(final String value, final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(value, String.class);
            Assert.exists(errors, String.class);
        }

        try {
            return Converters.convert(m_type, value);
        } catch (ConversionException ce) {
            errors.add(new ParameterError(this, ce));
            return null;
        }
    }

    /**
     * Calls {@link #doValidate(Object,ErrorList)} if
     * <code>value</code> is not null.  Otherwise, if the value is
     * <em>required and null</em>, an error is added to
     * <code>errors</code> and <code>doValidate</code> is not called.
     *
     * @see Parameter#validate(Object,ErrorList)
     */
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

    /**
     * Validates <code>value</code>, placing any validation errors in
     * <code>errors</code>.  This particular implementation does
     * nothing.  Subclasses are expected to add specific validation
     * behaviors.
     *
     * @param value The value to validate; it cannot be null
     * @param errors The <code>ErrorList</code> that traps validation
     * errors; it cannot be null
     */
    protected void doValidate(final Object value, final ErrorList errors) {
        if (Assert.isEnabled()) {
            Assert.exists(value, Object.class);
            Assert.exists(errors, ErrorList.class);
        }

        // Nothing
    }

    /**
     * Calls {@link #doWrite(ParameterWriter,Object)}.
     *
     * @see Parameter#write(ParameterWriter,Object)
     */
    public final void write(final ParameterWriter writer, final Object value) {
        Assert.exists(writer);

        // XXX what to do about nulls here?

        doWrite(writer, value);
    }

    /**
     * Marshals and writes <code>value</code> to <code>writer</code>.
     *
     * This implementation is suited to a parameter with a singular
     * scalar value.  Subclasses that are compound parameters should
     * override this method to delegate to child parameters.
     *
     * @param writer The <code>ParameterWriter</code> we write to; it
     * cannot be null
     * @param value The value to write; it may be null
     */
    protected void doWrite(final ParameterWriter writer, final Object value) {
        writer.write(this, marshal(value));
    }

    /**
     * Converts <code>value</code> to a representative
     * <code>String</code>, which is returned.
     *
     * @param value The value to marshal; it may be null
     * @return The <code>String</code> literal representation of
     * <code>value</code>; it may be null
     */
    protected String marshal(final Object value) {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    /**
     * Returns a <code>String</code> representation of this object.
     *
     * @return <code>super.toString() + "," + getName() + "," +
     * isRequired()</code>
     */
    public String toString() {
        return super.toString() + "," + getName() + "," + isRequired();
    }
}
