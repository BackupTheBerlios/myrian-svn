/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.tools.junit.extensions;

import junit.framework.Test;
import junit.framework.TestSuite;

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

/**
 * CoreTestSetup
 *
 * Utility extension of BaseTestSetup that automatically adds the core initializer
 *
 */
public class CoreTestSetup extends BaseTestSetup {
    public CoreTestSetup(Test test, TestSuite suite) {
        super(test, suite);
    }

    public CoreTestSetup(TestSuite suite) {
        super(suite);
    }

    protected void setUp() throws Exception {
        addRequiredInitializer("com.arsdigita.core.Initializer");
        super.setUp();
    }
}
