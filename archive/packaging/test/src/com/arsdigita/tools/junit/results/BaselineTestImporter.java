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

package com.arsdigita.tools.junit.results;

import java.util.Map;

/**
 *  BaselineTestImporter
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #2 $ $Date Nov 6, 2002 $
 */
public interface BaselineTestImporter {

    /**
     * Imports all the test result files for a particular changelist.
     *
     * @param changelist - The changelist number
     * @return Collection of org.jdom.Document
     */
    Map getTestsForChangelist(String changelist);

    /**
     * Imports all the test result files for the defined regression baseline.
     * Optional. May throw UnsupportedMethodException
     *
     * @return Collection of org.jdom.Document
     */
    Map getBaselineTests();

    /**
     * Returns true if this importer supports test baselines, and one is available.
     *
     * @return true if a baseline is available.
     */
    boolean isBaselineAvailable();
}
