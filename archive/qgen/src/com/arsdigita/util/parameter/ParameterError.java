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

/**
 * Subject to change.
 *
 * Information about an error for a parameter.  Parameter implementors
 * will add <code>ParameterError</code>s to the passed in
 * <code>ErrorList</code> when their parameters encounter error
 * conditions.
 *
 * @see ErrorList
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/ParameterError.java#1 $
 */
public final class ParameterError {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/ParameterError.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private final Parameter m_param;
    private final String m_message;
    private Throwable m_throwable;

    /**
     * Constructs a parameter error for <code>param</code>.
     *
     * @param param The <code>Parameter</code> whose value is in
     * error; it cannot be null
     * @param message A <code>String</code> description of the error
     */
    public ParameterError(final Parameter param,
                          final String message) {
        if (Assert.isEnabled()) {
            Assert.exists(param, Parameter.class);
            Assert.exists(message, String.class);
        }

        m_param = param;
        m_message = message;
    }

    /**
     * Constructs a parameter error for <code>param</code>, drawing
     * its error message from <code>throwable</code>.
     *
     * @param param The <code>Parameter</code> whose value is in
     * error; it cannot be null
     * @param throwable The <code>Throwable</code> for the error; it
     * cannot be null
     */
    public ParameterError(final Parameter param,
                          final Throwable throwable) {
        this(param, throwable.getMessage());

        m_throwable = throwable;
    }

    /**
     * Gets the parameter associated with this error.
     *
     * @return The <code>Parameter</code> in error; it cannot be null
     */
    public final Parameter getParameter() {
        return m_param;
    }

    /**
     * Gets the message associated with this error.
     *
     * @return The <code>String</code> message for the error; it
     * cannot be null
     */
    public final String getMessage() {
        // XXX this actually can be null, so need to prevent that
        return m_message;
    }

    /**
     * Gets the throwable, if present, that corresponds to the error.
     *
     * @return The <code>Throwable</code> of this error; it may be
     * null
     */
    public final Throwable getThrowable() {
        return m_throwable;
    }

    /**
     * Returns a string representation of the error suitable for
     * debugging.
     *
     * @return <code>super.toString() + "," + param.getName()</code>
     */
    public String toString() {
        return super.toString() + "," + m_param.getName();
    }
}
