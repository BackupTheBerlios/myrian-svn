/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.StringUtils;

/**
 * StringArrayParameter
 */
public class StringArrayParameter extends StringParameter {

    public StringArrayParameter(final String name,
                         final int multiplicity,
                         final Object defaalt) {
        super(name, multiplicity, defaalt);

    }

    protected Object unmarshal(final String literal,
                               final ErrorList errors) {
        final String[] literals = StringUtils.split(literal, ',');
        final String[] strings = new String[literals.length];

        for (int i = 0; i < literals.length; i++) {
            final String elem = literals[i];

            strings[i] = (String) super.unmarshal(elem, errors);

            if (!errors.isEmpty()) {
                break;
            }
        }
        return strings;
    }

    protected void doValidate(final Object value,
                              final ErrorList errors) {
        if (value != null) {
            final String[] strings = (String[]) value;

            for (int i = 0; i < strings.length; i++) {
                super.doValidate(strings[i], errors);

                if (!errors.isEmpty()) {
                    break;
                }
            }
        }
    }
}
