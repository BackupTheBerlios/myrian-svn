package com.arsdigita.persistence.pdl;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.metadata.DDLWriter;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.Table;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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


/**
 * TestPDLGenerator
 *
 */
public class TestPDLGenerator {

    /**
     * Compiles pdl files specifically for tests. Organizes the generation & output of SQL
     *
     * @throws PDLException if we have too few input files or if we
     * detect an error while parsing an input file. The reason we use
     * an exception is for the build process within ant to fail on
     * error.
     **/
    public static final void main(String[] args) throws PDLException {

        Map options = new HashMap();
        args = PDL.CMD.parse(options, args);

        BasicConfigurator.configure();
        if (Boolean.TRUE.equals(options.get("-debug"))) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        } else if (Boolean.TRUE.equals(options.get("-verbose"))) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else if (Boolean.TRUE.equals(options.get("-quiet"))) {
            Logger.getRootLogger().setLevel(Level.ERROR);
        } else {
            Logger.getRootLogger().setLevel(Level.FATAL);
        }

        String database = (String) options.get("-database");
        if ("postgres".equalsIgnoreCase(database)) {
            DbHelper.setDatabase(DbHelper.DB_POSTGRES);
        } else {
            DbHelper.setDatabase(DbHelper.DB_ORACLE);
        }

        List library = PDL.findPDLFiles((File[]) options.get("-library-path"));
        List files = PDL.findPDLFiles((File[]) options.get("-path"));
        files.addAll(Arrays.asList(args));

        if (files.size() < 1) {
            throw new PDLException(PDL.CMD.usage());
        }

        File debugDir = (File) options.get("-generate-events");
        if (debugDir != null) {
            if (!debugDir.exists() || !debugDir.isDirectory()) {
                throw new PDLException("No such directory: " + debugDir);
            }
            PDL.setDebugDirectory(debugDir);
        }


        Map map = getTestDirectoryMapping(files);
        for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {

            String directory = (String) iterator.next();
            List filesForDirectory = (List) map.get(directory);
            Set all = new HashSet();
            all.addAll(library);
            all.addAll(filesForDirectory);

            MetadataRoot.clear();
            PDL.compilePDLFiles(all);

            generateSQL(directory, filesForDirectory, options);
        }

    }

    private static Map getTestDirectoryMapping(List files) {
        HashMap map = new HashMap();
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            String file = (String) iterator.next();
            String directory = file.substring(0, file.lastIndexOf('/'));
            List dirList = (List) map.get(directory);
            if (null == dirList) {
                dirList = new LinkedList();
                map.put(directory, dirList);
            }
            dirList.add(file);
        }

        return map;

    }

    private static void generateSQL(String directory, List files, Map options) throws PDLException {
        MetadataRoot root = MetadataRoot.getMetadataRoot();

        String ddlDir = (String) options.get("-generate-ddl");
        if (ddlDir != null) {

            String subdir = directory.substring(directory.indexOf("/com/"));
            ddlDir += subdir;
            Set sqlFiles = new HashSet();
            File sqldir = (File) options.get("-sqldir");
            if (sqldir != null) {
                PDL.findSQLFiles(sqldir, sqlFiles);
            }
            File file = new File(ddlDir);
            file.mkdirs();

            DDLWriter writer = new DDLWriter(ddlDir, sqlFiles);

            writer.setTestPDL(true);

            List tables = new ArrayList(root.getTables());
            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                if (!files.contains(table.getFilename())) {
                    it.remove();
                }
            }
            try {
                writer.write(tables);
            } catch (IOException ioe) {
                throw new PDLException(ioe.getMessage());
            }


        }
    }

}
