/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util.parameter;

import com.arsdigita.util.Assert;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An exception to indicate invalid parameter states.  This exception
 * should only be used when the client code of a parameter opts in to
 * using exceptions rather than handling parameter errors itself.  See
 * {@link com.arsdigita.util.parameter.ErrorList#check()}.
 *
 * @see com.arsdigita.util.parameter.ErrorList
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterException.java#5 $
 */
public final class ParameterException extends RuntimeException {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterException.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterException.class);

    private final ErrorList m_errors;

    /**
     * Constructs a new parameter exception with the content
     * <code>message</code>.
     *
     * @param message A <code>String</code> describing what's wrong;
     * it cannot be null
     * @param errors The <code>ErrorList</code> containing the errors
     * that prompted this exception; it cannot be null
     */
    public ParameterException(final String message, final ErrorList errors) {
        super(message);

        if (Assert.isEnabled()) {
            Assert.exists(message, String.class);
            Assert.exists(errors, List.class);
        }

        m_errors = errors;
    }

    /**
     * Gets the set of errors associated with the exception.
     *
     * @return The <code>ErrorList</code> of errors; it cannot be null
     */
    public final ErrorList getErrors() {
        return m_errors;
    }
}
