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

package com.arsdigita.util;

import com.arsdigita.logging.*;
import com.arsdigita.util.config.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 * @see com.arsdigita.util.Util
 */
final class UtilConfig extends ParameterRecord {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/UtilConfig.java#5 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

    private static final Logger s_log = Logger.getLogger(UtilConfig.class);

    private final Parameter m_dir;

    UtilConfig() {
        super("util");

        // XXX need conf URL support here
        m_dir = new StringParameter
            ("waf.util.logging.error_report_dir", Parameter.OPTIONAL, null);

        register(m_dir);
    }

    final String getErrorReportDirectory() {
        return (String) get(m_dir);
    }

    final void setAssertEnabled(final boolean isEnabled) {
        System.setProperty("waf.util.assert_enabled", null);
    }
}
