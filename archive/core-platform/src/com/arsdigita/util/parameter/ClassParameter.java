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

import org.apache.commons.beanutils.converters.ClassConverter;

/**
 * Subject to change.
 *
 * A parameter representing a Java <code>Class</code>.
 *
 * @see java.lang.Class
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#7 $
 */
public class ClassParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#7 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    static {
        Converters.set(Class.class, new ClassConverter());
    }

    public ClassParameter(final String name) {
        super(name, Class.class);
    }

    public ClassParameter(final String name,
                          final int multiplicity,
                          final Object defaalt) {
        super(name, multiplicity, defaalt, Class.class);
    }

    // value != null
    protected Object unmarshal(String value, ErrorList errors) {
        Class theClass = null;
        try {
            theClass = Class.forName(value);
        } catch (ClassNotFoundException e) {
            errors.add(new ParameterError(this, "No such class: " + value));
        }

        return theClass;
    }

    protected String marshal(Object value) {
        Class theClass = ((Class) value);
        if (theClass == null) {
            return null;
        } else {
            return theClass.getName();
        }
    }
}
