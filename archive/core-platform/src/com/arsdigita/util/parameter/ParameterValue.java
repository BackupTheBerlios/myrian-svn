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



/**
 * @deprecated The parameter APIs no longer need this class.
 */
public final class ParameterValue {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterValue.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

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
