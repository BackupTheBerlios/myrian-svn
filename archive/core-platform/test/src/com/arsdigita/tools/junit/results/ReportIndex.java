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

import org.jdom.Element;

/**
 *  ReportIndex
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #3 $ $Date Nov 6, 2002 $
 */
public class ReportIndex extends Element {
    public ReportIndex(String previousChangelist, String currentChangelist) {
        super("junit_index");
        setAttribute("previous_changelist", previousChangelist);
        setAttribute("current_changelist", currentChangelist);
        setAttribute("changes", "0");
    }

    public void addResult(ResultDiff diff) {
        Element elem = new Element("test");
        elem.setAttribute("name", diff.getTestName());


        elem.setAttribute("tests", Integer.toString(diff.getCurrent().getTestCount()));
        int testDelta = diff.getCurrent().getTestCount() - diff.getPrevious().getTestCount();
        elem.setAttribute("test_delta", Integer.toString(testDelta));

        elem.setAttribute("failures", Integer.toString(diff.getCurrent().getFailureCount()));
        int failureDelta = diff.getCurrent().getFailureCount() - diff.getPrevious().getFailureCount();
        elem.setAttribute("failure_delta", Integer.toString(failureDelta));

        elem.setAttribute("errors", Integer.toString(diff.getCurrent().getErrorCount()));
        int errorDelta = diff.getCurrent().getErrorCount() - diff.getPrevious().getErrorCount();
        elem.setAttribute("error_delta", Integer.toString(errorDelta));

        elem.setAttribute("new_tests", Integer.toString(diff.newTestCount()));
        elem.setAttribute("missing_tests", Integer.toString(diff.missingTestCount()));

        final boolean warning = testDelta < 0 || failureDelta > 0 || errorDelta > 0 || diff.missingTestCount() > 0;

        elem.setAttribute("warning", "" + warning);

        final boolean changesExist = !(testDelta == 0 &&
                failureDelta == 0 && errorDelta == 0 && diff.missingTestCount() == 0 && diff.newTestCount() == 0);

        if (changesExist) {
            int changes = Integer.parseInt(getAttributeValue("changes"));
            changes++;
            setAttribute("changes", Integer.toString(changes));
        }

        addContent(elem);
    }
}
