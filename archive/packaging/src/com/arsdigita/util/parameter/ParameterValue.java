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
 * @deprecated The parameter APIs no longer need this class.
 */
public final class ParameterValue {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterValue.java#8 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/20 23:01:49 $";

    private final ErrorList m_errors;
    private String m_string;
    private Object m_object;

    public ParameterValue() {
        m_errors = new ErrorList();
    }

    public final ErrorList getErrors() {
        return m_errors;
    }

    public final String getString() {
        return m_string;
    }

    public final void setString(final String string) {
        m_string = string;
    }

    public final Object getObject() {
        return m_object;
    }

    public final void setObject(final Object value) {
        m_object = value;
    }
}
