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
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * An error to indicate invalid configurations.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterException.java#1 $
 */
public final class ParameterException extends RuntimeException {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterException.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/23 14:12:40 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterException.class);

    private final List m_errors;

    /**
     * Constructs a new parameter exception with the content
     * <code>message</code>.
     *
     * @param message A <code>String</code> describing what's wrong;
     * it cannot be null
     */
    public ParameterException(final String message, final List errors) {
        super(message);

        if (Assert.isEnabled()) {
            Assert.exists(message, String.class);
            Assert.exists(errors, List.class);
        }

        m_errors = errors;
    }

    public final List getErrors() {
        return m_errors;
    }
}
