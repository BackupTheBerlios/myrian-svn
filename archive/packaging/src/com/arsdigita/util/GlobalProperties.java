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

import org.apache.log4j.Logger;

/**
 * A facade class for Java system properties.
 *
 * @author Justin Ross
 */
public final class GlobalProperties {
    public static final String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/GlobalProperties.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/20 13:36:34 $";

    private static final Logger s_log = Logger.getLogger(GlobalProperties.class);

    /**
     * Gets the value of a global property.  Returns null if there is
     * no such property.
     */
    public static final String get(final String key) {
        return System.getProperty(key);
    }

    /**
     * Sets a global property to <code>value</code>.
     */
    public static final void set(final String key, final String value) {
        System.setProperty(key, value);
    }
}
