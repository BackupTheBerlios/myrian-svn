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

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameterContext.java#1 $
 */
public abstract class AbstractParameterContext implements ParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/AbstractParameterContext.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/17 11:35:44 $";

    private final ArrayList m_params;
    private final HashMap m_values;

    public AbstractParameterContext() {
        m_params = new ArrayList();
        m_values = new HashMap();
    }

    public final void register(final Parameter param) {
        m_params.add(param);
    }

    public final Object get(final Parameter param) {
        Assert.exists(param, Parameter.class);

        return m_values.get(param);
    }

    public final void set(final Parameter param, final Object value) {
        Assert.exists(param, Parameter.class);

        m_values.put(param, value);
    }

    public final ErrorList load(ParameterReader reader) {
        final ErrorList errors = new ErrorList();

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            //set(param, param.read(reader, errors));
        }

        return errors;
    }

    public final ErrorList validate() {
        final ErrorList errors = new ErrorList();

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            //param.validate(get(param), errors);
        }

        return errors;
    }

    public final void save(ParameterWriter writer) {
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            //param.write(writer, get(param));
        }
    }
}
