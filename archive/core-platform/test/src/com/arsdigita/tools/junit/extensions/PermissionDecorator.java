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

package com.arsdigita.tools.junit.extensions;

import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestResult;
import com.arsdigita.kernel.*;
import com.arsdigita.util.Assert;

/**
 *
 * @author Jon Orris (jorris@redhat.com)
 * @version $Revision: #3 $ $DateTime: 2003/10/15 13:49:15 $
 */
public class PermissionDecorator extends TestDecorator {
    public PermissionDecorator(Test test) {
        super(test);
    }

    public void run(TestResult testResult) {
        final TestResult finalResult = testResult;
        KernelExcursion ex = new KernelExcursion() {

            protected void excurse() {
                setParty(getAdminUser());
                PermissionDecorator.super.run(finalResult);
            }
        };

        ex.run();
    }

    public static User getAdminUser() {
        UserCollection uc = User.retrieveAll();

        try {
            uc.filter(KernelHelper.getSystemAdministratorEmailAddress());
            uc.next();
            User sysadmin = uc.getUser();
            Assert.exists(sysadmin, User.class);

            return sysadmin;
        } finally {
            uc.close();
        }
    }

}
