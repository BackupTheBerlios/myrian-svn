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

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.Converter;

/**
 * Subject to change.
 *
 * Collects together BeanUtils converters for use by the base
 * <code>Parameter</code>s.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/Converters.java#3 $
 */
public class Converters {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/Converters.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:17:07 $";

    private static Map s_converters = Collections.synchronizedMap
        (new HashMap());

    /**
     * Gets the <code>Converter</code> registered for
     * <code>clacc</code>.  This method will fail if no converter is
     * found.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @return A <code>Converter</code> instance; it cannot be null
     */
    public static final Converter get(final Class clacc) {
        Assert.exists(clacc, Class.class);

        final Converter converter = (Converter) s_converters.get(clacc);

        Assert.exists(converter, Converter.class);

        return converter;
    }

    /**
     * Registers <code>converter</code> for <code>clacc</code>.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @param converter The <code>Converter</code> to register to
     * <code>clacc</code>; it cannot be null
     */
    public static final void set(final Class clacc, final Converter converter) {
        if (Assert.isEnabled()) {
            Assert.exists(clacc, Class.class);
            Assert.exists(converter, Converter.class);
        }

        s_converters.put(clacc, converter);
    }

    /**
     * Converts <code>value</code> using the converter registered for
     * <code>clacc</code>.
     *
     * @param clacc The <code>Class</code> of the parameter value; it
     * cannot be null
     * @param value The <code>String</code>-encoded value of the
     * parameter; it may be null
     * @return The Java object conversion for <code>value</code>; it
     * may be null
     */
    public static final Object convert(final Class clacc, final String value) {
        Assert.exists(clacc, Class.class);

        return get(clacc).convert(clacc, value);
    }
}
