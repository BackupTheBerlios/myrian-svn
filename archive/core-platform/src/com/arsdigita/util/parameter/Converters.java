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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/Converters.java#1 $
 */
class Converters {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/Converters.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/09 14:53:22 $";

    private static Map s_converters = Collections.synchronizedMap
        (new HashMap());

    static final Converter get(final Class clacc) {
        final Converter converter = (Converter) s_converters.get(clacc);

        Assert.exists(converter, Converter.class);

        return converter;
    }

    static final void set(final Class clacc, final Converter converter) {
        s_converters.put(clacc, converter);
    }

    static final Object convert(final Class clacc, final String value) {
        return get(clacc).convert(clacc, value);
    }
}
