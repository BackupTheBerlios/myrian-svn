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
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/MapParameter.java#2 $
 */
public class MapParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/MapParameter.java#2 $" +
        "$Author: rhs $" +
        "$DateTime: 2003/10/21 23:00:37 $";

    private final ArrayList m_params;

    public MapParameter(final String name,
                        final int multiplicity,
                        final Object defaalt) {
        super(name, multiplicity, defaalt, String.class);

        m_params = new ArrayList();
    }

    public MapParameter(final String name) {
        super(name, String.class);

        m_params = new ArrayList();
    }

    public final void add(final Parameter param) {
        Assert.exists(param, Parameter.class);

        m_params.add(param);
    }

    public final boolean contains(final Parameter param) {
        Assert.exists(param, Parameter.class);

        return m_params.contains(param);
    }

    public final Iterator iterator() {
        return m_params.iterator();
    }

    public final Object read(final ParameterReader reader,
                             final ErrorList errors) {
        final HashMap map = new HashMap();

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            Object value = param.read(reader, errors);
            if (value != null) {
                map.put(param, value);
            }
        }

        return map;
    }

    public final void validate(final Object value, final ErrorList errors) {
        final HashMap map = (HashMap) value;

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();
            if (map.containsKey(param)) {
                param.validate(map.get(param), errors);
            }
        }
    }

    public final void write(final ParameterWriter writer, final Object value) {
        final HashMap map = (HashMap) value;

        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            param.write(writer, map.get(param));
        }
    }
}
