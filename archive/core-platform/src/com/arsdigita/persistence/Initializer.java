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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.metadata.MDSQLGeneratorFactory;
import com.arsdigita.util.ResourceManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.pdl.PDL;
import com.arsdigita.persistence.pdl.PDLException;

import java.io.File;
import java.io.FileFilter;
import java.io.StringReader;

import java.util.List;
import java.util.ArrayList;

import java.lang.IllegalArgumentException;

import org.apache.log4j.Logger;


/**
 * Initializer gets the information required to create Sessions and informs
 * the SessionManager of them.
 *
 * @author Archit Shah (ashah@arsdigita.com)
 * @version $Revision: #4 $ $Date: 2002/08/13 $
 **/

public class Initializer
    implements com.arsdigita.initializer.Initializer {

    private static final Logger s_log =
         Logger.getLogger(Initializer.class.getName());

    private Configuration m_conf = new Configuration();

    public Initializer() throws InitializationException {
        m_conf.initParameter("pdlDirectory",
                             "The directory where a server's PDL files are " +
                             "stored.", 
			     String.class,
			     "/WEB-INF/pdl");

        m_conf.initParameter("mdsqlGenerator",
                             "The MDSQLGenerator implementation to use",
                             String.class);

        m_conf.initParameter("aggressiveConnectionClose",
                             "Aggressively soft-close connections as soon as " +
                             "there are no users when connection has not " +
                             "been used to modify data",
                             Boolean.class);
        m_conf.initParameter("optimizeByDefault",
                             "Use the optimizing query generator by default.",
                             Boolean.class,
                             ObjectType.getOptimizeDefault() ?
                             Boolean.TRUE : Boolean.FALSE);

        // here for legacy purposes
        m_conf.initParameter("metadataXmlFileNames", 
                             "The names of the xml file defining the " +
                             "persistence metadata. This file must be in " +
                             "your classpath", List.class);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Sets up the session and loads the persistence metadata from a
     * file somewhere in your classpath. The name of the file to which
     * to search for is defined by the metadataXmlFileName
     * initialization parameter.
     **/
    public void startup() {
        s_log.warn("Persistence initializer is starting");

        // Set the utilities
        if (com.arsdigita.db.Initializer.getDatabase() == com.arsdigita.db.Initializer.POSTGRES) {
            Session.setSQLUtilities(new PostgresSQLUtilities());
        } else {
            Session.setSQLUtilities(new OracleSQLUtilities());
        }
        ObjectType.setOptimizeDefault(
            m_conf.getParameter("optimizeByDefault").equals(Boolean.TRUE)
            );

        try {
            MDSQLGeneratorFactory.setMDSQLGenerator(
                (String)m_conf.getParameter("mdsqlGenerator"));
        } catch (Exception e) {
            throw new InitializationException(
                "Error instantiating MDSQLGenerator");
        }

        String pdlDir = (String)m_conf.getParameter("pdlDirectory");

        if ((m_conf.getParameter("metadataXmlFileNames") != null) && 
            (pdlDir == null)) {
            throw new InitializationException(
                "Invalid persistence configuration.  Please replace " +
                "metadataXmlFileNames with pdlDirectory.");
        }

        /*Boolean aggressiveClose = 
          Boolean.valueOf(((String)m_conf.getParameter("aggressiveConnectionClose")));*/
        Boolean aggressiveClose = 
                (Boolean)m_conf.getParameter("aggressiveConnectionClose");
        if (aggressiveClose != null && aggressiveClose.booleanValue()) {
            s_log.info("Using aggressive connection closing");
            TransactionContext.setAggressiveClose(true);
        } else {
            s_log.info("Not using aggressive connection closing " + 
                       "[aggressiveConnectionClose parameter]");
        }

        SessionManager.setSchemaConnectionInfo( "",  "", "", "");

        ResourceManager rm = ResourceManager.getInstance();

        File webAppRoot = rm.getWebappRoot();
        File dir;
        
        // If we're not running inside a webapp, we don't want the wrong
        // thing to happen.
        if ( webAppRoot == null ) {
            dir = new File(pdlDir);
        } else {
            dir = new File(webAppRoot + pdlDir);
        }

        List files = new ArrayList();

        findPDLFiles(dir, files);

        s_log.warn("Found " + files.size() + " files in the " + dir.toString() + " directory.");

        try {
            PDL.compilePDL(files);
        } catch (PDLException e) {
                throw new InitializationException
                    ("Persistence Initialization error while trying to " +
                     "compile the PDL files: " + e.getMessage());
        }

        // now we load the files out of the database
        try {
            Session session = SessionManager.getSession();
            TransactionContext txn = session.getTransactionContext();
            txn.beginTxn();

            MetadataRoot root = MetadataRoot.getMetadataRoot();
            PDL pdl = new PDL();
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
                    s_log.warn
                        ("The Object Type [" + currentFile + "] has already " +
                         "been defined in the static files.  Ignoring " + 
                         "object type definition from the database");
                    continue;
                }

                String pdlFile = (String)collection.get("pdlFile");
                pdl.load(new StringReader(pdlFile),
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
                pdl.load(new StringReader(pdlFile),
                         "DATABASE: " + currentFile);
            }

            pdl.generateMetadata(root);

            //try {
            // Future use -- Patrick
            //PDLOutputter.writePDL(root, new java.io.File("/tmp/pdl"));
            //} catch (java.io.IOException e) {
            //System.out.println(e.getMessage());
            //}

            txn.commitTxn();
        } catch (PDLException e) {
                throw new InitializationException
                    ("Persistence Initialization error while trying to " +
                     "compile the PDL files: " + e.getMessage());
        }
    }

    private void findPDLFiles(File dir, List files) {
        if (!dir.exists()) {
            return;
        }

        if (!dir.isDirectory()) {
            files.add(dir.toString());
            return;
        }

        File[] contents = dir.listFiles(new PDLFileFilter());
		
        for (int i = 0; i < contents.length; i++) {
            findPDLFiles(contents[i], files);
        }
    }

    public void shutdown() {}
}

/**
 * Filter out files that do not end in ".pdl".
 */
class PDLFileFilter implements FileFilter {
	public boolean accept(File file) {
		if (file.isDirectory() || file.getName().endsWith("pdl")) {
			return true;
		} else {
			return false;
		} 
	}
}

