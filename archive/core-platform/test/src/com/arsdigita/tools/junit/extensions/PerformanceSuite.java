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

import com.arsdigita.tools.junit.extensions.TimedTestRecord;
import com.arsdigita.tools.junit.extensions.PerfTiming;
import com.arsdigita.tools.junit.framework.PackageTestSuite;

import com.arsdigita.util.UncheckedWrapperException;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * <P> TestSuite used to run performance tests using the {@link TimedTestRecord}
 * test decorator. Only actualy TestCases should be added to this suite,
 * <i>not</i> TestSuites or TestDecorators.</P>
 *
 * <P> The PerformanceSuite stores runtime data relating to each of it's tests
 * in an xml file. The data from previous runs is used as a basis for the
 * timout value of each subsequent run. Therefore tests that take significantly
 * longer than they used to are failed.</P>
 *
 * @see TimedTestRecord
 * @see PerfTiming
 */

public class PerformanceSuite extends PackageTestSuite {
    private PerfTiming m_perfTiming;

    public PerformanceSuite() {
        super();
        m_perfTiming = new PerfTiming();

        try {
            File file = getTimingFile();
            m_perfTiming.load(file);
        } catch (Exception e) {
            throw new UncheckedWrapperException(
                    "Error loading performance file", e);
        }
    }

    /*
     * Performance timing results will be associated with a specific
     * test suite. All performace tests run by the suite wil be stored.
     * Timing results file will be same name as suite.
     */
    private File getTimingFile() {
        String path = System.getProperty("test.base.dir") + "/"
                      + getClass().getName().replace('.','/')
                      + "Timing.xml";
        File file = new File(path);
        return file;
    }


    /* Create the TimedTestRecord when tests are added to the suite. */
    public void addTest(Test test) {
        Test timedTest = new TimedTestRecord((TestCase)test, m_perfTiming);
        super.addTest(timedTest);   //To change body of overriden
    }
}
