/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
