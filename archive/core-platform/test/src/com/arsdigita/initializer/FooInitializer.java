/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
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
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 */

public class FooInitializer implements Initializer {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/initializer/FooInitializer.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

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
