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
import com.arsdigita.util.UncheckedWrapperException;

import com.clarkware.junitperf.TimedTest;

import junit.extensions.TestDecorator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * <P>This test decorator is used to decorate <code>TestCases</code> only.
 * It records how long the tests took to execute. If the test had been 
 * run previously it uses the previous 'time to completion' as a basis for
 * the timeout. I.e. if the test took 'significantly' longer than before, 
 * it will fail. {@link PerfTiming}'s <code>variance</code> field for further
 * details.</P>
 *
 * @see PerfTiming
 * @see TestCaseDescriptor
 *
 * @author <a href="mailto:aahmed@redhat.com"> Aizaz Ahmed </a>
 */
public class TimedTestRecord extends TestDecorator {

    private PerfTiming pTime;
    
    public TimedTestRecord ( TestCase test, PerfTiming perfTiming ) {
        super ( test );
        pTime = perfTiming;
    }
    
    public TimedTestRecord ( TestCase test ) {
        super ( test );
        try {
            pTime = new PerfTiming();
            pTime.load();
        } catch (Exception e) {
            throw new UncheckedWrapperException(
                            "Error loading performance file", e);
        }
    }

    public void run(TestResult result) {

        try {
            /* get the previous recorded time, new timeout value */
            TestCaseDescriptor tdesc = pTime.getDescriptor ( fTest );
            long timeout = tdesc.getFastestWithVar();

            /*
             * we need to record the time ourselves as well,
             * unfortunately TimedTest does not give us access to the 
             * beginning time
             */
            TimedTest timedTest = new TimedTest ( fTest, timeout );
            long beginTime = System.currentTimeMillis();
            timedTest.run ( result );
            long elapsed = System.currentTimeMillis() - beginTime;

            /* update the records only if an error did not occur */
            if  ( result.wasSuccessful () ) { 
                if ( elapsed < tdesc.getFastest() ) {
                    tdesc.setFastest ( elapsed );
                    pTime.update ( tdesc );
                }
            } else {
            }
            
        } catch ( Exception e ) {
            /* an unexpected exception */
            result.addError ( fTest, new Error ( e.toString() ) );
        }
    }
}
