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

package com.arsdigita.tools.junit.results;

import java.util.Map;

/**
 *  FileTestImporter
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #1 $ $Date Nov 6, 2002 $
 */
public class FileTestImporter implements BaselineTestImporter {


    public Map getTestsForChangelist(String changelist) {
        ResultFileSetLoader loader = new ResultFileSetLoader();
        String archiveDir = System.getProperty("junit.result.archive");
        if (!archiveDir.endsWith("/")) {
            archiveDir += "/";
        }

        Map tests = loader.loadResultFiles(archiveDir + changelist);

        return tests;
    }

    public Map getBaselineTests() {
        throw new UnsupportedOperationException(getClass() + " does not support baselines. Use getTestsForChangelist");
    }

    public boolean isBaselineAvailable() {
        return false;
    }

}
