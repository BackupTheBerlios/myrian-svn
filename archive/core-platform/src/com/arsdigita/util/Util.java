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
 * <p>An entry point for the services of the util package.</p>
 *
 * @author Justin Ross
 * @see com.arsdigita.util.UtilConfig
 */
final class Util {
    public static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/Util.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/04/13 00:26:01 $";

    private static final Logger s_log = Logger.getLogger(Util.class);

    private static final UtilConfig s_config = new UtilConfig();

    /**
     * Returns the util config record.
     *
     * @post return != null
     */
    public static final UtilConfig getConfig() {
        return s_config;
    }
}
