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

package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDL;
import com.arsdigita.persistence.pdl.PDLOutputter;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * PersistenceTestCase
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2003/05/12 $
 */

public class PersistenceTestCase extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/PersistenceTestCase.java#10 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    private static final Logger LOG =
        Logger.getLogger(PersistenceTestCase.class);

    // Prevent loading the same PDL file twice
    private static Set s_loadedPDLResources = new HashSet();

    /**
     *  This loads the passed in resource.  It also checks for the existence
     *  of files with the same name for the specific database that is being
     *  used.  It does that by locating the substring "testpdl" and
     *  replacing it with "testpdl/<database-here>" such as
     *  "testpdl/oracle-se"
     *  @param outputPDL If true and if the value of "outputPDLDir" within
     *  the source code itself has been changed then it will output
     *  the PDL.  This should eventaully be fixed to take in a config
     *  file parameter so that the source code does not need to change.
     */
    protected static void load(String resource, boolean outputPDL) {
        if (s_loadedPDLResources.contains(resource)) {
            return;
        }

        s_loadedPDLResources.add(resource);

        String shadow = resource.substring(0, resource.lastIndexOf('.'));
        String ext = resource.substring(resource.lastIndexOf('.') + 1,
                                        resource.length());
        shadow = shadow + "." + DbHelper.getDatabaseSuffix() + "." + ext;

        try {
            PDL m = new PDL();

            String[] resources = new String[] {shadow, resource};
            for (int i = 0; i < resources.length; i++) {
                if (m.getClass().getClassLoader().getResourceAsStream
                    (resources[i]) != null) {
                    m.loadResource(resources[i]);
                    s_loadedPDLResources.add(resources[i]);
                    break;
                }
            }

            m.generateMetadata(MetadataRoot.getMetadataRoot());

            String outputPDLDir = null;
            if (outputPDL) {
                try {
                    PDLOutputter.writePDL(MetadataRoot.getMetadataRoot(),
                                          new java.io.File(outputPDLDir));
                } catch (java.io.IOException e) {
                    System.err.println
                        ("There was a problem generating debugging output");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        s_loadedPDLResources.add(resource);
    }


    /**
     *  This loads the passed in resource.  It also checks for the existence
     *  of files with the same name for the specific database that is being
     *  used.  It does that by locating the substring "testpdl" and
     *  replacing it with "testpdl/<database-here>" such as
     *  "testpdl/oracle-se"
     */
    protected static void load(String resource) {
        load(resource, false);
    }

    public PersistenceTestCase(String name) {
        super(name);
    }

    /**
     * Runs the bare test sequence.
     *
     * @exception Throwable if any exception is thrown
     **/

    public void runBare() throws Throwable {
        persistenceSetUp();
        try {
            super.runBare();
            m_session.getProtoSession().flushAll();
        } finally {
            persistenceTearDown();
        }
    }

    protected void persistenceSetUp() {
        LOG.warn("Starting " + getClass().getName() + "." + getName());
        m_session = SessionManager.getSession();
        m_session.getTransactionContext().beginTxn();
    }

    protected void persistenceTearDown() {
        try {
            if (m_session.getTransactionContext().inTxn()) {
                m_session.getTransactionContext().abortTxn();
            }
        } finally {
            LOG.warn("Ending " + getClass().getName() + "." + getName());
        }
    }

    protected Session getSession() {
        return m_session;
    }

    private Session m_session;

}
