/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util;

import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 * @see com.arsdigita.util.Util
 */
final class UtilConfig extends Record {
    public static final String versionId =
        "$Id: //core-platform/proto/src/com/arsdigita/util/UtilConfig.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/04 16:15:53 $";

    private static final Logger s_log = Logger.getLogger(UtilConfig.class);

    private boolean m_isAssertEnabled = true;

    private static final String[] s_fields = new String[] {
        "AssertEnabled"
    };

    UtilConfig() {
        super(UtilConfig.class, s_log, s_fields);
    }

    public final boolean isAssertEnabled() {
        return m_isAssertEnabled;
    }

    final void setAssertEnabled(boolean enabled) {
        m_isAssertEnabled = enabled;

        mutated("AssertEnabled");
    }
}
