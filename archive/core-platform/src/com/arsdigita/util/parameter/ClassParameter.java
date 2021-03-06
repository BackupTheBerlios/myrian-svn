/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#9 $
 */
public class ClassParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#9 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/08/16 18:10:38 $";

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
