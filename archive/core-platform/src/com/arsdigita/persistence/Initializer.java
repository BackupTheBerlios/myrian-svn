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
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.metadata.MDSQLGeneratorFactory;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.pdl.PDL;
import com.arsdigita.persistence.pdl.PDLException;

import java.io.File;
import java.io.StringReader;


import java.lang.IllegalArgumentException;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Initializer gets the information required to create Sessions and informs
 * the SessionManager of them.
 *
 * @author Archit Shah (ashah@arsdigita.com)
 * @version $Revision: #8 $ $Date: 2002/09/30 $
 **/

public class Initializer
    implements com.arsdigita.initializer.Initializer {

    private static final Logger s_log =
        Logger.getLogger(Initializer.class.getName());

    private Configuration m_conf = new Configuration();

    public static final String PDL_DIRECTORY =
        "pdlDirectory";
    public static final String AGGRESSIVE_CONNECTION_CLOSE =
        "aggressiveConnectionClose";
    public static final String OPTIMIZE_BY_DEFAULT =
        "optimizeByDefault";
    public static final String METADATA_XML_FILE_NAMES =
        "metadataXmlFileNames";

    public static final String SESSION_FACTORY = "sessionFactory";

    public Initializer() throws InitializationException {
        m_conf.initParameter(PDL_DIRECTORY,
                             "The directory where a server's PDL files are " +
                             "stored.",
                             String.class,
                             "/WEB-INF/pdl");

        m_conf.initParameter(AGGRESSIVE_CONNECTION_CLOSE,
                             "Aggressively soft-close connections as soon as " +
                             "there are no users when connection has not " +
                             "been used to modify data",
                             Boolean.class);
        m_conf.initParameter(OPTIMIZE_BY_DEFAULT,
                             "Use the optimizing query generator by default.",
                             Boolean.class,
                             ObjectType.getOptimizeDefault() ?
                             Boolean.TRUE : Boolean.FALSE);

        // here for legacy purposes
        m_conf.initParameter(METADATA_XML_FILE_NAMES,
                             "The names of the xml file defining the " +
                             "persistence metadata. This file must be in " +
                             "your classpath", List.class);

        m_conf.initParameter(SESSION_FACTORY,
                             "Class name of the Session factory to use",
                             String.class,
                            DefaultSessionFactory.class.getName());
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

        int database = DbHelper.getDatabase();

        // Set the utilities
        if (database == DbHelper.DB_ORACLE) {
            SessionManager.setSQLUtilities(new OracleSQLUtilities());
        } else if (database == DbHelper.DB_POSTGRES) {
            SessionManager.setSQLUtilities(new PostgresSQLUtilities());
        } else {
            DbHelper.unsupportedDatabaseError("SQL Utilities");
        }
        ObjectType.setOptimizeDefault(
                                      m_conf.getParameter(OPTIMIZE_BY_DEFAULT).equals(Boolean.TRUE)
                                      );

        if (database == DbHelper.DB_ORACLE) {
            MDSQLGeneratorFactory.setMDSQLGenerator(
                                                    MDSQLGeneratorFactory.ORACLE_GENERATOR
                                                    );
        } else if (database == DbHelper.DB_POSTGRES) {
            MDSQLGeneratorFactory.setMDSQLGenerator(
                                                    MDSQLGeneratorFactory.POSTGRES_GENERATOR
                                                    );
        } else {
            DbHelper.unsupportedDatabaseError("MDSQL generator");
        }

        String pdlDir = (String)m_conf.getParameter(PDL_DIRECTORY);

        if ((m_conf.getParameter(METADATA_XML_FILE_NAMES) != null) &&
            (pdlDir == null)) {
            throw new InitializationException(
                                              "Invalid persistence configuration.  Please replace " +
                                              "metadataXmlFileNames with pdlDirectory.");
        }


        SessionManager.setSchemaConnectionInfo( "",  "", "", "");
        final SessionFactory factory = getSessionFactory();
        SessionManager.setSessionFactory(factory);


        Boolean aggressiveClose =
            (Boolean)m_conf.getParameter(AGGRESSIVE_CONNECTION_CLOSE);
        if (aggressiveClose != null && aggressiveClose.booleanValue()) {
            s_log.info("Using aggressive connection closing");
            factory.setAggressiveConnectionClose(true);
        } else {
            s_log.info("Not using aggressive connection closing " +
                       "[aggressiveConnectionClose parameter]");
        }

        //SessionManager.setSessionFactory();
        PDL.loadPDLFiles(new File(pdlDir));

        // Finally the files out of the database
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

    private SessionFactory getSessionFactory() {
        SessionFactory factory;
        final String factoryClassName = (String) m_conf.getParameter(SESSION_FACTORY);
        try {
            factory = (SessionFactory) Class.forName(factoryClassName).newInstance();

        } catch(ClassNotFoundException cne) {
            throw new InitializationException("No such SessionFactory implementation: " + factoryClassName, cne);
        } catch(InstantiationException ie) {
            throw new InitializationException("Could not instantiate SessionFactory " + factoryClassName +
                    " reason: " + ie.getMessage(), ie);
        } catch(IllegalAccessException ia) {
            throw new InitializationException("Could not instantiate SessionFactory " + factoryClassName +
                    " due to private constructor! ", ia);
        }
        return factory;
    }


    public void shutdown() {}
}
