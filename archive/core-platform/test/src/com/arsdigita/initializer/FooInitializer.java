/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.initializer;

import java.util.*;

/**
 * A test Initializer
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 */

public class FooInitializer implements Initializer {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/initializer/FooInitializer.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private static boolean isStarted = false;

    public static boolean isStarted() {
        return isStarted;
    }

    Configuration m_config = new Configuration();

    public FooInitializer() throws InitializationException {
        m_config.initParameter("stringParam", "This is a usage string.",
                               String.class,"This is a string.");
        m_config.initParameter("objectParam", "A dummy object.", Object.class);
        m_config.initParameter("listParam", "Should be a list.",
                               java.util.List.class, new ArrayList());
    }

    public Configuration getConfiguration() {
        return m_config;
    }

    public void startup() {
        isStarted = true;
    }

    public void shutdown() {
        isStarted = false;
    }

}
