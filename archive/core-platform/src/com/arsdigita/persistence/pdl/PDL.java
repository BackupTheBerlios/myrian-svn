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

package com.arsdigita.persistence.pdl;


import com.arsdigita.util.StringUtils;
import com.arsdigita.util.ResourceManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.Assert;

import com.arsdigita.persistence.pdl.ast.AST;
import com.arsdigita.persistence.Utilities;
import com.arsdigita.persistence.metadata.DDLWriter;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.oql.Query;
import com.arsdigita.db.DbHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * The main class that is used to process PDL files.  It takes any number of
 * PDL files as arguments on the command line, then processes them all into
 * a single XML file (the first command line argument).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #11 $ $Date: 2002/08/14 $
 */

public class PDL {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDL.java#11 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    private static final Logger s_log = Logger.getLogger(PDL.class);

    // the abstract syntax tree root nod
    private AST m_ast = new AST();

    public PDL() {
    }

    /**
     * Retrieve a reference to the abstract syntax tree generated from the
     * PDL files.
     *
     * @return a reference to the AST
     */
    public AST getAST() {
        return m_ast;
    }

    /**
     * Generates the metadata that corresponds to the AST generated from the
     * various PDL files, all beneath the given metadata root node.
     *
     * @param root the metadata root node to build the metadata beneath
     */
    public void generateMetadata(MetadataRoot root) {
        m_ast.generateMetadata(root);
    }

    /**
     * Parses a PDL file into an AST.
     *
     * @param r a Reader open to the PDL file
     * @param filename the name of the PDL file read by "r"
     * @throws PDLException thrown on a parsing error.
     */
    public void load(Reader r, String filename) throws PDLException {
        try {
            Parser p = new Parser(r);
            p.file(m_ast, filename);
        } catch (ParseException e) {
            throw new PDLException(e.getMessage());
        }
    }

    /**
     * Parse a PDL file into an AST.
     *
     * @param f a File object that references a PDL file to parse
     * @throws PDLException thrown when the file is not found or on a parse
     *                      error
     */
    public void load(File f) throws PDLException {
        try {
            load(new FileReader(f), f.toString());
        } catch (FileNotFoundException e) {
            throw new PDLException(e.getMessage());
        }
    }

    /**
     * Parse a PDL file into an AST.
     *
     * @param filename the name of the PDL file to parse
     * @throws PDLException on file not found or a parse error.
     */
    public void load(String filename) throws PDLException {
        load(new File(filename));
    }

    /**
     *
     * @param s
     * @pre s != null
     * @throws PDLException
     */
    public void loadResource(String s) throws PDLException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(s);
        if (is == null) {
            throw new PDLException("No such resource: " + s);
        }
        load(new InputStreamReader(is), s);
    }

    // This map is for arguments that expect values. The default value is
    // stored in this map. If the default value is a boolean that the option
    // is assumed to be a flag.
    private static final Map OPTIONS = new HashMap();

    static {
        OPTIONS.put("-debugDirectory", null);
        OPTIONS.put("-path", null);
        OPTIONS.put("-dot", null);
        OPTIONS.put("-ddl", null);
        OPTIONS.put("-destination", null);
        OPTIONS.put("-database", null);
        OPTIONS.put("-sqldir", null);
        OPTIONS.put("-debug", Boolean.FALSE);
        OPTIONS.put("-verbose", Boolean.FALSE);
    }

    /**
     * Compiles pdl files into one xml file. The target xml file is
     * the first argument. All other arguments refer to pdl files that
     * need to be loaded.
     *
     * @throws PDLException if we have too few input files or if we
     * detect an error while parsing an input file. The reason we use
     * an exception is for the build process within ant to fail on
     * error.
     **/
    public static final void main(String[] argArray) throws PDLException {
        List args = new ArrayList(java.util.Arrays.asList(argArray));

        while (args.size() > 0) {
            String arg = (String) args.get(0);
            if (OPTIONS.containsKey(arg)) {
                args.remove(0);
                Object value = OPTIONS.get(arg);
                if (Boolean.FALSE.equals(value)) {
                    OPTIONS.put(arg, Boolean.TRUE);
                } else if (Boolean.TRUE.equals(value)) {
                    OPTIONS.put(arg, Boolean.FALSE);
                } else if (args.size() > 0) {
                    OPTIONS.put(arg, args.get(0));
                    args.remove(0);
                } else {
                    usage();
                }
            } else {
                break;
            }
        }

        BasicConfigurator.configure();
        if (Boolean.TRUE.equals(OPTIONS.get("-debug"))) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        } else if (Boolean.TRUE.equals(OPTIONS.get("-verbose"))) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else {
            Logger.getRootLogger().setLevel(Level.FATAL);
        }

        String debugDir = (String) OPTIONS.get("-debugDirectory");
        if (debugDir != null) {
            setDebugDirectory(debugDir);
        }

        String database = (String) OPTIONS.get("-database");
        if ("postgres".equalsIgnoreCase(database)) {
            DbHelper.setDatabase(DbHelper.DB_POSTGRES);
        } else {
            DbHelper.setDatabase(DbHelper.DB_ORACLE);
        }

        String defaultDir = DbHelper.getDatabaseDirectory(DbHelper.DB_DEFAULT);
        String databaseDir = DbHelper.getDatabaseDirectory(DbHelper.getDatabase());


        List files = new ArrayList();
        String path = (String) OPTIONS.get("-path");
        if (path != null) {
            String[] parts = StringUtils.split(path, ':');
            for (int i = 0; i < parts.length; i++) {
                s_log.debug("Loading default PDL files from " + defaultDir);
                findPDLFiles(new File(parts[i] + "/" + defaultDir), files);

                s_log.debug("Loading database PDL files from " + databaseDir);
                findPDLFiles(new File(parts[i] + "/" + databaseDir), files);
            }
        }

        files.addAll(args);

        if (files.size() < 1) {
            usage();
        } else {
            compilePDLFiles(files);

            String ddlDir = (String) OPTIONS.get("-ddl");
            if (ddlDir != null) {
                Set sqlFiles = new HashSet();
                String sqldir = (String)OPTIONS.get("-sqldir");
                findSQLFiles(new File(sqldir + "/" + defaultDir), sqlFiles);
                findSQLFiles(new File(sqldir + "/" + databaseDir), sqlFiles);

                DDLWriter writer = new DDLWriter(ddlDir, sqlFiles);

                try {
                    writer.write(MetadataRoot.getMetadataRoot());
                } catch (IOException ioe) {
                    throw new PDLException(ioe.getMessage());
                }
            }

            String dot = (String) OPTIONS.get("-dot");
            if (dot != null) {
                String[] parts = StringUtils.split(dot, ':');
                if (parts.length > 2) {
                    throw new PDLException(
                                           "Badly formated specification: " + dot
                                           );
                }
                MetadataRoot root = MetadataRoot.getMetadataRoot();
                ObjectType type = root.getObjectType(parts[0]);
                if (type == null) {
                    throw new PDLException("No such type: " + parts[0]);
                }
                Query query = new Query(type);
                if (parts.length == 2) {
                    query.fetch(parts[1]);
                } else {
                    query.fetchDefault();
                }
                query.generate();
                query.dumpDot(new File("/tmp/out.dot"));
            }
        }
    }

    private static final void usage() throws PDLException {
        // Use a string buffer to build up our error messages.
        StringBuffer sb = new StringBuffer();
        sb.append("Usage: ").append(PDL.class.getName());
        sb.append(" <pdl file> <pdl-file> ..." + Utilities.LINE_BREAK);

        throw new PDLException(sb.toString());
    }

    private static String s_debugDirectory = null;

    public static void setDebugDirectory(String directory) {
        s_debugDirectory = directory;
    }

    public static String getDebugDirectory() {
        return s_debugDirectory;
    }


    /**
     * Loads all the PDL files in a given directory
     */
    public static void loadPDLFiles(File dir) {
        ResourceManager rm = ResourceManager.getInstance();

        File webAppRoot = rm.getWebappRoot();

        // If we're not running inside a webapp, we don't want the wrong
        // thing to happen.
        try {
            if ( webAppRoot != null ) {
                dir = new File(webAppRoot.getCanonicalPath() + dir);
            }
        } catch (IOException e) {
            throw new UncheckedWrapperException("cannot get file path", e);
        }

        List files = findPDLFiles(dir);
        s_log.warn("Found " + files.size() + " files in the " +
                   dir.toString() + " directory.");

        try {
            compilePDLFiles(files);
        } catch (PDLException ex) {
            throw new UncheckedWrapperException
                ("Persistence Initialization error while trying to " +
                 "compile the PDL files", ex);
        }
    }

    /**
     * Finds all PDL files in a given directory
     */
    public static List findPDLFiles(File dir) {
        List files = new ArrayList();
        findFiles(dir, files, ".pdl", false);
        return files;
    }

    /**
     * Searches a directory for all PDL files
     */
    public static void findPDLFiles(File base,
                                    Collection files) {
        findFiles(base, files, ".pdl", false);
    }

    public static void findSQLFiles(File base,
                                    Collection files) {
        findFiles(base, files, ".sql", true);
    }

    public static void findFiles(File base,
                                 Collection files,
                                 final String extension,
                                 boolean trimPath) {
        if (!base.exists()) {
            s_log.warn("Skipping directory " + base + " since it doesn't exist");
            return;
        }

        Assert.assertTrue(base.isDirectory(), "directory " + base + " is directory");

        Stack dirs = new Stack();
        dirs.push(base);

        while (dirs.size() > 0) {
            File dir = (File) dirs.pop();
            File[] listing = dir.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isDirectory() ||
                            file.getName().endsWith(extension);
                    }
                });
            for (int i = 0; i < listing.length; i++) {
                if (listing[i].isDirectory()) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Found subdir " + listing[i]);
                    }
                    dirs.push(listing[i]);
                } else {
                    try {
                        String path = listing[i].getCanonicalPath();
                        if (trimPath) {
                            int index = path.lastIndexOf("/");
                            if (index != -1) {
                                path = path.substring(index + 1);
                            }
                        }
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("Found file " + path);
                        }
                        files.add(path);
                    } catch (IOException e) {
                        throw new UncheckedWrapperException(
                                                            "cannot get file path", e
                                                            );
                    }
                }
            }
        }
    }


    /**
     * Compiles PDL to Persistence Metadata
     *
     * @param files array of PDL files to process
     */
    public static void compilePDLFiles(List files)
        throws PDLException {
        StringBuffer sb = new StringBuffer();
        PDL pdl = new PDL();

        for (int i = 0; i < files.size(); i++) {
            try {
                pdl.load((String)files.get(i));
            } catch (PDLException e) {
                sb.append((String)files.get(i)).append(": ");
                sb.append(e.getMessage()).append(Utilities.LINE_BREAK);
            }
        }

        if (sb.length() == 0) {
            // No  errors so far. Try generating the xml file.
            pdl.generateMetadata(MetadataRoot.getMetadataRoot());
            if (s_debugDirectory != null) {
                try {
                    PDLOutputter.writePDL(MetadataRoot.getMetadataRoot(),
                                          new java.io.File(s_debugDirectory));
                } catch (java.io.IOException ex) {
                    s_log.error(
                                "There was a problem generating debugging output", ex
                                );
                }
            }
        } else {
            throw new PDLException(sb.toString());
        }
    }
}
