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
 * Subject to Change
 *
 * This encapsulates the information for a single parameter.  This is
 * typically created and populated by a ParameterRecord
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterValue.java#6 $
 */
public final class ParameterValue {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterValue.java#6 $" +
        "$Author: randyg $" +
        "$DateTime: 2003/10/10 16:04:50 $";

    private final ArrayList m_errors;
    private String m_string;
    private Object m_object;

    public ParameterValue() {
        m_errors = new ArrayList();
    }

    public final List getErrors() {
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
