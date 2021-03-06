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
 * A container of parameters.  A parameter context binds together a
 * set of parameters and keeps their values.
 *
 * @see com.arsdigita.util.parameter.Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/ParameterContext.java#1 $
 */
public interface ParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/ParameterContext.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    /**
     * Returns all the parameters registered on the parameter context.
     *
     * @return A <code>Parameter[]</code> of all the parameters; it
     * cannot be null
     */
    Parameter[] getParameters();

    /**
     * Gets the unmarshaled value of <code>param</code>.  If the
     * loaded value is null, <code>param.getDefaultValue()</code> is
     * returned.
     *
     * @param param The named <code>Parameter</code> whose value to
     * retrieve; it cannot be null
     * @return The unmarshaled Java object value of <code>param</code>
     */
    Object get(Parameter param);

    /**
     * Gets the unmarshaled value of <code>param</code>, returning
     * <code>dephalt</code> if <code>param</code>'s value is null.
     *
     * @param param The <code>Parameter</code> whose value to
     * retrieve; it cannot be null
     * @param dephalt The fallback default value; it may be null
     * @return The unmarshaled Java object value of <code>param</code>
     * or <code>dephalt</code> if the former is null
     */
    Object get(Parameter param, Object dephalt);

    /**
     * Sets the value of <code>param</code> to <code>value</code>.
     *
     * @param param The <code>Parameter</code> whose value to set; it
     * cannot be null
     * @param value The new value of <code>param</code>; it may be
     * null
     */
    void set(Parameter param, Object value);

    /**
     * Reads and unmarshals all values associated with the registered
     * parameters from <code>reader</code>.  If any errors are
     * encountered, they are added to <code>errors</code>.
     *
     * @param reader The <code>ParameterReader</code> from which to
     * fetch the values; it cannot be null
     * @param errors The <code>ErrorList</code> that captures any
     * errors while loading; it cannot be null
     */
    void load(ParameterReader reader, ErrorList errors);

    /**
     * Marshals and writes all values associated with the registered
     * parameters to <code>writer</code>.
     *
     * @param writer The <code>ParameterWriter</code> to which values
     * are written; it cannot be null
     */
    void save(ParameterWriter writer);

    /**
     * Validates all values associated with the registered parameters.
     * Any errors encountered are added to <code>errors</code>.
     *
     * @param errors The <code>ErrorList</code> that captures
     * validation errors; it cannot be null
     */
    void validate(ErrorList errors);
}
