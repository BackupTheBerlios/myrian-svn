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

import org.jdom.Element;

/**
 *  ReportIndex
 *
 *  @author <a href="mailto:jorris@redhat.com">Jon Orris</a>
 *  @version $Revision: #2 $ $Date Nov 6, 2002 $
 */
public class ReportIndex extends Element {
    public ReportIndex(String previousChangelist, String currentChangelist, String databaseType) {
        super("junit_index");
        setAttribute("previous_changelist", previousChangelist);
        setAttribute("current_changelist", currentChangelist);
        setAttribute("database_type", databaseType);
        setAttribute("changes", "0");
        setAttribute("warnings", "0");
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

        final boolean warningsExist = testDelta < 0 || failureDelta > 0 || errorDelta > 0 || diff.missingTestCount() > 0;

        elem.setAttribute("warning", "" + warningsExist);
		
        final boolean changesExist = !(testDelta == 0 &&
                failureDelta == 0 && errorDelta == 0 && diff.missingTestCount() == 0 && diff.newTestCount() == 0);

        if (changesExist) {
            incrementChangeCount();
        } 
        
        if(warningsExist) {
            incrementWarningCount();
        }

        addContent(elem);
    }

    public void incrementWarningCount() {
        int warnings = Integer.parseInt(getAttributeValue("warnings"));
        warnings++;
        setAttribute("warnings", Integer.toString(warnings));
    }

    public void incrementChangeCount() {
        int changes = Integer.parseInt(getAttributeValue("changes"));
        changes++;
        setAttribute("changes", Integer.toString(changes));
    }
}
