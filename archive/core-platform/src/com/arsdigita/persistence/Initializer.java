/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.pdl.PDLCompiler;
import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.runtime.RuntimeConfig;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * Initializer loads UDCTs.
 *
 * @author Archit Shah
 * @version $Revision: #22 $ $Date: 2004/05/28 $
 **/

public class Initializer implements com.arsdigita.runtime.Initializer {

    private static final Logger s_log =
        Logger.getLogger(Initializer.class.getName());

    public void init(DataInitEvent evt) { }

    /**
     * Sets up the session and loads the persistence metadata from a
     * file somewhere in your classpath. The name of the file to which
     * to search for is defined by the metadataXmlFileName
     * initialization parameter.
     **/
    public void init(DomainInitEvent evt) {
        com.redhat.persistence.oql.Query.setQueryCacheSize( RuntimeConfig.getConfig().getQueryCacheSize() );

        // Finally the files out of the database
        TransactionContext txn = null;
        try {
            Session session = SessionManager.getSession();
            txn = session.getTransactionContext();
            txn.beginTxn();

            MetadataRoot root = MetadataRoot.getMetadataRoot();
            PDLCompiler pdl = new PDLCompiler();
            DataCollection collection = SessionManager.getSession()
                .retrieve("com.arsdigita.persistence.DynamicObjectType");
            while (collection.next()) {
                String currentFile = (String)collection.get("dynamicType");
                if (s_log.isInfoEnabled()) {
                    s_log.info("loading... " + currentFile.toString());
                }

                if (root.getObjectType(currentFile) != null) {
                    // this means that there is a type in the database
                    // that has already been defined so we write an error
                    if (s_log.isEnabledFor(Level.WARN)) {
                        s_log.warn
                            ("The Object Type [" + currentFile + "] has already " +
                             "been defined in the static files.  Ignoring " +
                             "object type definition from the database");
                    }
                    continue;
                }

                String pdlFile = (String)collection.get("pdlFile");
                pdl.parse(new StringReader(pdlFile),
                          "DATABASE: " + currentFile);
            }

            collection = SessionManager.getSession()
                .retrieve("com.arsdigita.persistence.DynamicAssociation");

            while (collection.next()) {
                String currentFile = "Association from " +
                    collection.get("objectType1") + " to " +
                    collection.get("objectType2");

                // XXX: I'm going to ignore the possibility of this error for
                // now.
                //s_log.warn("An association from " + currentFile + " has " +
                //"already been defined.  Ignoring version in " +
                //"the database");

                if (s_log.isInfoEnabled()) {
                    s_log.info("loading " + currentFile);
                }

                String pdlFile = (String)collection.get("pdlFile");
                pdl.parse(new StringReader(pdlFile),
                          "DATABASE: " + currentFile);
            }

            pdl.emit(root);

            //try {
            // Future use -- Patrick
            //PDLOutputter.writePDL(root, new java.io.File("/tmp/pdl"));
            //} catch (java.io.IOException e) {
            //System.out.println(e.getMessage());
            //}

            txn.commitTxn();
        } finally {
            if (txn != null && txn.inTxn()) {
                txn.abortTxn();
            }
        }
    }

    public void init(LegacyInitEvent evt) { }
}
