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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterError.java#1 $
 */
public final class ParameterError {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterError.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/23 15:28:18 $";

    private final Parameter m_param;
    private final String m_message;
    private Throwable m_throwable;

    public ParameterError(final Parameter param,
                          final String message) {
        m_param = param;
        m_message = message;
    }

    public ParameterError(final Parameter param,
                          final Throwable throwable) {
        this(param, throwable.getMessage());

        m_throwable = throwable;
    }

    public final Parameter getParameter() {
        return m_param;
    }

    public final String getMessage() {
        return m_message;
    }

    // Can be null
    public final Throwable getThrowable() {
        return m_throwable;
    }

    /**
     * @return <code>super.toString() + "," + param.getName()</code>
     */
    public String toString() {
        return super.toString() + "," + m_param.getName();
    }
}
