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
import org.apache.commons.beanutils.converters.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#3 $
 */
public class ClassParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ClassParameter.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/24 13:22:48 $";

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
        return theClass.getName();
    }
}
