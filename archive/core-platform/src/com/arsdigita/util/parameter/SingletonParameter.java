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

import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.List;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/SingletonParameter.java#3 $
 */
public class SingletonParameter extends ClassParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/SingletonParameter.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/24 13:22:48 $";

    public SingletonParameter(final String name) {
        super(name);
    }

    public SingletonParameter(final String name,
                              final int multiplicity,
                              final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected String marshal(Object value) {
        return super.marshal(value.getClass());
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        final Class clacc = (Class) super.unmarshal(value, errors);
        if(clacc == null) {
            return null;
        }

        try {
            return Classes.newInstance(clacc);
        } catch (UncheckedWrapperException uwe) {
            errors.add(new ParameterError(this, uwe.getRootCause()));
            return null;
        }
    }
}
