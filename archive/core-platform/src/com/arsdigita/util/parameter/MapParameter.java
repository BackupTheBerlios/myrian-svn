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

import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Subject to change.
 *
 * A parameter that manages a collection of <code>Parameter</code> to
 * <code>Object</code> value mappings.
 *
 * @see java.util.Map
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/MapParameter.java#3 $
 */
public class MapParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/MapParameter.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:56:08 $";

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

    protected Object doRead(final ParameterReader reader,
                            final ErrorList errors) {
        final HashMap map = new HashMap();
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();
            final Object value = param.read(reader, errors);

            if (value != null) {
                map.put(param, value);
            }
        }

        return map;
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        final HashMap map = (HashMap) value;
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            if (map.containsKey(param)) {
                param.validate(map.get(param), errors);
            } else {
                param.validate(param.getDefaultValue(), errors);
            }
        }
    }

    protected void doWrite(final ParameterWriter writer, final Object value) {
        final HashMap map = (HashMap) value;
        final Iterator params = m_params.iterator();

        while (params.hasNext()) {
            final Parameter param = (Parameter) params.next();

            if (map.containsKey(param)) {
                param.write(writer, map.get(param));
            }
        }
    }
}
