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

import org.apache.commons.beanutils.converters.ClassConverter;

/**
 * Subject to change.
 *
 * A parameter representing a Java <code>Class</code>.
 *
 * @see java.lang.Class
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#6 $
 */
public class ClassParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#6 $" +
        "$Author: dan $" +
        "$DateTime: 2004/01/06 12:49:45 $";

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
