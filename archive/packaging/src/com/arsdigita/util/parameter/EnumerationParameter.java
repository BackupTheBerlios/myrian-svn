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
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/EnumerationParameter.java#3 $
 */
public class EnumerationParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/EnumerationParameter.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/28 00:48:42 $";

    private static final Logger s_log = Logger.getLogger
        (EnumerationParameter.class);

    private final HashMap m_entries;

    public EnumerationParameter(final String name) {
        super(name);

        m_entries = new HashMap();
    }

    public final void addEntry(final String name, final Object value) {
        m_entries.put(name, value);
    }

    protected Object unmarshal(final String value, final List errors) {
        if (m_entries.containsKey(value)) {
            return m_entries.get(value);
        } else {
            errors.add("The value must be one of " + m_entries.keySet());

            return null;
        }
    }
}
