/*
 * Copyright (C) 2001, 2002, 2003, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.db;

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DedicatedConnectionSource;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.Protectable;
import junit.extensions.TestDecorator;

/**
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/02/25 $
 */
public class DBTestSuite extends PackageTestSuite {
    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/arsdigita/db/DBTestSuite.java#2 $ by $Author: richardl $, $DateTime: 2004/02/25 09:03:46 $";

    public static Test suite() {
        DBTestSuite suite = new DBTestSuite();
        populateSuite(suite);
        //BaseTestSetup wrapper = new CoreTestSetup(suite);
        //return wrapper;
        TestDecorator sessionSetup = new TestDecorator(suite) {
            public void run(final TestResult result) {
                final Protectable p = new Protectable() {
                    public void protect() throws Exception {
                        final String key = "default";
                        String url = RuntimeConfig.getConfig().getJDBCURL();
                        final MetadataRoot root = MetadataRoot.getMetadataRoot();
                        SessionManager.configure(key, root, new DedicatedConnectionSource(url));

                        basicRun(result);
                    }

                };

                result.runProtected(this, p);
            }
        };
        return sessionSetup;
    }

}
