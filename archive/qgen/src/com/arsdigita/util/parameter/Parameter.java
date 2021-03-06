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

/**
 * Subject to change.
 *
 * Describes a named property that can read, write, and validate its
 * own value.  See the documentation on {@link #read}, {@link #write},
 * and {@link #validate} for details.

 * They have the following features as well:
 *
 * <ul>
 *   <li>Multiplicity.  A parameter can be nullable (0..x) or required
 *   (1..x) and singular (x..1) or multiple (x..n).  The current
 *   parameter implementation only models nullablel vs. required
 *   parameters.</li>
 *
 *   <li>Defaulting.  A parameter can have a value to fall back on if
 *   none is set.</li>
 *
 *   <li>Optional metadata.  Optional extra "info" can be associated
 *   with a parameter.</li>
 * </ul>
 *
 * The read and validate phases of a parameter collect errors into a
 * list so that calling code can control error handling.  This is in
 * lieu of throwing exceptions that are not useful in creating
 * error-recovery UIs.
 *
 * In contrast, the write phase of a parameter is expected to complete
 * successfully or fail outright.
 *
 * Parameters are stateless "messages".  They do not store their own
 * values.  Instead, a {@link
 * com.arsdigita.util.parameter.ParameterContext} manages a set of
 * parameters and keeps their values.
 *
 * Here's what it typically looks like to use a parameter:
 *
 * <blockquote><code>
 * Properties props = System.getProperties();
 * ParameterReader reader = JavaPropertyReader(props);
 * ParameterWriter writer = JavaPropertyWriter(props);
 * ErrorList errors = new ErrorList();
 *
 * Object value = param.read(reader, errors);
 * errors.check(); // If errors is not empty, fails
 *
 * param.validate(value, errors);
 * errors.check(); // If errors is not empty, fails
 *
 * // We now have a valid unmarshaled value, so code of actual use can
 * // go here.
 *
 * param.write(writer, value);
 * </code></blockquote>
 *
 * @see com.arsdigita.util.parameter.AbstractParameter
 * @see com.arsdigita.util.parameter.ParameterContext
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/Parameter.java#1 $
 */
public interface Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/Parameter.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    /**
     * Flag to indicate the parameter value is nullable.
     */
    public static final int OPTIONAL = 0;

    /**
     * Flag to indicate the parameter value cannot be null.
     */
    public static final int REQUIRED = 1;

    /**
     * Tells wether the parameter is nullable or not.
     *
     * @return true if the parameter cannot be null; false if it can
     * be null
     */
    boolean isRequired();

    /**
     * Gets the name of the parameter.
     *
     * @return The <code>String</code> parameter name; it cannot be
     * null
     */
    String getName();

    /**
     * Gets the default value of the parameter.  Implementations may
     * choose to substitute this value for null.
     *
     * @return The fallback value; it may be null
     */
    Object getDefaultValue();

    /**
     * Gets metadata associated with the parameter if it is available.
     *
     * @return The <code>ParameterInfo</code> object; it may be null
     */
    ParameterInfo getInfo();

    /**
     * Sets the optional parameter metadata to <code>info</code>.
     *
     * @param info The <code>ParameterInfo</code> to associate; it may
     * be null
     */
    void setInfo(ParameterInfo info);

    /**
     * Gets the parameter value as a Java object. The value will have
     * a specific runtime type and so may be appropriately cast.
     *
     * Reading typically follows the following procedure:
     *
     * <ul>
     *   <li>Read the literal string value associated with the
     *   parameter from <code>reader</code></li>
     *
     *   <li>Convert the literal string value into an approprite Java
     *   object</li>
     * </ul>
     *
     * If at any point in the process an error is encountered, it is
     * added to <code>errors</code>.  Callers of this method will
     * typically construct an <code>ErrorList</code> in which to
     * collect errors.
     *
     * @param reader The <code>ParameterReader</code> from which to
     * recover a string literal value; it cannot be null
     * @param errors The <code>ErrorList</code> in which to collect
     * any errors encountered; it cannot be null
     * @return The Java object value of the parameter
     */
    Object read(ParameterReader reader, ErrorList errors);

    /**
     * Validates the parameter value, <code>value</code>.  Any
     * validation errors encountered are added to <code>errors</code>.
     *
     * @param value The value to validate; this is typically the value
     * returned by {@link #read}; it may be null
     * @param errors The <code>ErrorList</code> in which to collect
     * any errors encountered; it cannot be null
     */
    void validate(Object value, ErrorList errors);

    /**
     * Writes the parameter value as a string literal.  The parameter
     * marshals he object <code>value</code> to a string and sends it
     * to <code>writer</code>.
     *
     * @param writer The <code>ParameterWriter</code> that will take
     * the marshaled value and store it; it cannot be null
     * @param value The Java object value of the parameter
     */
    void write(ParameterWriter writer, Object value);
}
