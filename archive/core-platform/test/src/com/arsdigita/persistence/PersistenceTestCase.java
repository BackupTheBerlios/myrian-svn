/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDL;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;
import java.io.File;
import java.io.InputStream;


/**
 * PersistenceTestCase
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #3 $ $Date: 2002/07/22 $
 */

public class PersistenceTestCase extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/PersistenceTestCase.java#3 $ by $Author: randyg $, $DateTime: 2002/07/22 11:33:00 $";

    // Prevent loading the same PDL file twice
    private static Set s_loadedPDLResources = new HashSet();

    /**
     *  This loads the passed in resource.  It also checks for the existence
     *  of files with the same name for the specific database that is being
     *  used.  It does that by locating the substring "testpdl" and 
     *  replacing it with "testpdl/<database-here>" such as 
     *  "testpdl/oracle-se"
     */
    protected static void load(String resource) {
        if (s_loadedPDLResources.contains(resource)) {
            return;
        }
        s_loadedPDLResources.add(resource);

        String extraResource = null;
        if (resource.indexOf("testpdl") > -1) {
            String prefix = resource.substring
                (0, resource.indexOf("testpdl") + 8);
            String suffix = resource.substring(resource.indexOf("testpdl") + 7);
            resource = prefix + "default" + suffix;
            if (com.arsdigita.db.Initializer.getDatabase() ==
                com.arsdigita.db.Initializer.POSTGRES) {
                extraResource = prefix + "postgres" + suffix;
            } else {
                extraResource = prefix + "oracle-se" + suffix;
            }
        }

        try {
            PDL m = new PDL();
            if (m.getClass().getClassLoader().getResourceAsStream
                (extraResource) != null) {
                m.loadResource(extraResource);
                s_loadedPDLResources.add(extraResource);
            }

            m.loadResource(resource);
            m.generateMetadata(MetadataRoot.getMetadataRoot());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        }
        s_loadedPDLResources.add(resource);
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
        } finally {
            persistenceTearDown();
        }
    }

    protected void persistenceSetUp() {
        m_session = SessionManager.getSession();
        m_session.getTransactionContext().beginTxn();
    }

    protected void persistenceTearDown() {
        m_session = SessionManager.getSession();
        if (m_session.getTransactionContext().inTxn()) {
            m_session.getTransactionContext().abortTxn();
        }
    }

    protected Session getSession() {
        return m_session;
    }

    private Session m_session;

}
