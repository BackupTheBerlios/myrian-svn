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

package com.arsdigita.util;


import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.ErrorList;
import java.util.Map;
import java.util.HashMap;

public class AliasedClassParameter extends ClassParameter {

    private Map m_aliases;
    
    public AliasedClassParameter(final String name,
                                 final int multiplicity,
                                 final Object defaalt) {
        super(name, multiplicity, defaalt);
        m_aliases = new HashMap();
    }

    public void addAlias(String alias,
                         String className) {
        m_aliases.put(alias, className);
    }

    protected Object unmarshal(String value, ErrorList errors) {
        if (m_aliases.containsKey(value)) {
            value = (String)m_aliases.get(value);
        }

        return super.unmarshal(value, errors);
    }
}
