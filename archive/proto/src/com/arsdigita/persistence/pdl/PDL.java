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
import com.arsdigita.util.cmd.*;

import com.arsdigita.persistence.pdl.ast.AST;
import com.arsdigita.persistence.Utilities;
import com.arsdigita.persistence.proto.pdl.DDLWriter;
import com.arsdigita.persistence.proto.metadata.Root;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.proto.metadata.Table;
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
import java.io.StringReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The main class that is used to process PDL files.  It takes any number of
 * PDL files as arguments on the command line, then processes them all into
 * a single XML file (the first command line argument).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #5 $ $Date: 2003/04/02 $
 */

public class PDL {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/PDL.java#5 $ by $Author: rhs $, $DateTime: 2003/04/02 12:28:31 $";

    private static final Logger s_log = Logger.getLogger(PDL.class);
    private static boolean s_quiet = false;

    // the abstract syntax tree root nod
    private AST m_ast = new AST();
    private com.arsdigita.persistence.proto.pdl.PDL m_pdl =
        new com.arsdigita.persistence.proto.pdl.PDL();
    private StringBuffer m_file = new StringBuffer(1024*10);

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
        m_pdl.emit(Root.getRoot());
        m_pdl.emitVersioned();
    }

    /**
     * Parses a PDL file into an AST.
     *
     * @param r a Reader open to the PDL file
     * @param filename the name of the PDL file read by "r"
     * @throws PDLException thrown on a parsing error.
     */
    public void load(Reader r, String filename) throws PDLException {
        m_file.setLength(0);
        char[] buf = new char[1024];
        int nchars;
        do {
            try {
                nchars = r.read(buf);
            } catch (IOException e) {
                throw new PDLException(e.getMessage());
            }
            if (nchars > 0) {
                m_file.append(buf, 0, nchars);
            }
        } while (nchars > 0);

        try {
            Parser p = new Parser(new StringReader(m_file.toString()));
            p.file(m_ast, filename);
            m_pdl.load(new StringReader(m_file.toString()), filename);
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

    private static final CommandLine CMD =
        new CommandLine(PDL.class.getName(), null);

    static {
        CMD.addSwitch(new PathSwitch(
            "-library-path",
            "PDL files appearing in this path will be searched " +
            "for unresolved dependencies found in the files to be processed",
            new File[0]
            ));
        CMD.addSwitch(new PathSwitch(
            "-path",
            "PDL files appearing in this path will be processed",
            new File[0]
            ));
        CMD.addSwitch(new BooleanSwitch("-validate", "validate PDL",
                                        Boolean.FALSE));
        CMD.addSwitch(new StringSwitch("-generate-ddl",
                                       "generate ddl and write " +
                                       "it to the specified directory", null));
        CMD.addSwitch(new FileSwitch(
            "-generate-events",
            "if present PDL will be written to the specified directory " +
            "containing the MDSQL generated events",
            null
            ));
        CMD.addSwitch(new StringSwitch("-dot", "undocumented", null));
        CMD.addSwitch(new StringSwitch("-database", "target database", null));
        CMD.addSwitch(new FileSwitch("-sqldir", "sql directory", null));
        CMD.addSwitch(new BooleanSwitch("-debug", "sets logging to DEBUG",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-verbose", "sets logging to INFO",
                                        Boolean.FALSE));
        CMD.addSwitch(new BooleanSwitch("-quiet", "sets logging to ERROR and does not complain if no PDL files are found",
                                        Boolean.FALSE));
        CMD.addSwitch(new StringSwitch("-testddl", "no clue", null));
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
    public static final void main(String[] args) throws PDLException {
        Map options = new HashMap();
        args = CMD.parse(options, args);

        BasicConfigurator.configure();
        if (Boolean.TRUE.equals(options.get("-debug"))) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        } else if (Boolean.TRUE.equals(options.get("-verbose"))) {
            Logger.getRootLogger().setLevel(Level.INFO);
        } else if (Boolean.TRUE.equals(options.get("-quiet"))) {
            Logger.getRootLogger().setLevel(Level.ERROR);
            s_quiet = true;
        } else {
            Logger.getRootLogger().setLevel(Level.FATAL);
        }

        String database = (String) options.get("-database");
        if ("postgres".equalsIgnoreCase(database)) {
            DbHelper.setDatabase(DbHelper.DB_POSTGRES);
        } else {
            DbHelper.setDatabase(DbHelper.DB_ORACLE);
        }

        List library = findPDLFiles((File[]) options.get("-library-path"));
        List files = findPDLFiles((File[]) options.get("-path"));
        files.addAll(Arrays.asList(args));

        if (files.size() < 1) {
            if (s_quiet) {
                return;
            } 
            usage();
        }

        File debugDir = (File) options.get("-generate-events");
        if (debugDir != null) {
            if (!debugDir.exists() || !debugDir.isDirectory()) {
                throw new PDLException("No such directory: " + debugDir);
            }
            setDebugDirectory(debugDir);
        }

        Set all = new HashSet();
        all.addAll(library);
        all.addAll(files);

        compilePDLFiles(all);

        MetadataRoot root = MetadataRoot.getMetadataRoot();

        String ddlDir = (String) options.get("-generate-ddl");
        if (ddlDir != null) {
            Set sqlFiles = new HashSet();
            File sqldir = (File) options.get("-sqldir");
            if (sqldir != null) {
                findSQLFiles(sqldir, sqlFiles);
            }

            DDLWriter writer = new DDLWriter(ddlDir, sqlFiles);

            if (Boolean.TRUE.equals(Boolean.valueOf((String) options.get("-testddl")))) {
                writer.setTestPDL(true);
            }

            List tables = new ArrayList(Root.getRoot().getTables());
            for (Iterator it = tables.iterator(); it.hasNext(); ) {
                Table table = (Table) it.next();
                if (!files.contains(Root.getRoot().getFilename(table))) {
                    it.remove();
                }
            }
            try {
                writer.write(tables);
            } catch (IOException ioe) {
                throw new PDLException(ioe.getMessage());
            }
        }

        String dot = (String) options.get("-dot");
        if (dot != null) {
            String[] parts = StringUtils.split(dot, ':');
            if (parts.length > 2) {
                throw new PDLException(
                    "Badly formated specification: " + dot
                    );
            }
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

    private static final void usage() throws PDLException {
        throw new PDLException(CMD.usage());
    }

    private static String s_debugDirectory = null;

    public static void setDebugDirectory(File directory) {
        s_debugDirectory = directory.getPath();
    }

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
     * Finds all the PDL files in a given path.
     **/

    public static List findPDLFiles(File[] path) {
        List result = new ArrayList();

        for (int i = 0; i < path.length; i++) {
            s_log.debug("Loading default PDL files from " + path[i]);
            findPDLFiles(path[i], result);
        }

        return result;
    }

    /**
     * Finds all PDL files in a given directory
     */
    public static List findPDLFiles(File dir) {
        List files = new ArrayList();
        findFiles(dir, files, "pdl", false);
        return files;
    }

    /**
     * Searches a directory for all PDL files
     */
    public static void findPDLFiles(File base, Collection files) {
        findFiles(base, files, "pdl", false);
    }

    public static void findSQLFiles(File base, Collection files) {
        findFiles(base, files, "sql", true);
    }

    private static final Set SUFFIXES = new HashSet();

    static {
        String[] sfxs = DbHelper.getDatabaseSuffixes();
        for (int i = 0; i < sfxs.length; i++) {
            SUFFIXES.add(sfxs[i]);
        }
    }

    private static void findFiles(File base, Collection files,
                                  final String extension,
                                  boolean trimPath) {
        if (!base.exists()) {
            s_log.warn("Skipping directory " + base +
                       " since it doesn't exist");
            return;
        }

        Assert.assertTrue(base.isDirectory(), "directory " + base +
                          " is directory");

        final String suffix = DbHelper.getDatabaseSuffix();
        Stack dirs = new Stack();
        dirs.push(base);
        Set toAdd = new HashSet();

        while (dirs.size() > 0) {
            File dir = (File) dirs.pop();
            File[] listing = dir.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        if (file.isDirectory()) {
                            return true;
                        }

                        String name = file.getName();
                        String base = base(name);
                        String sfx = suffix(name);
                        String ext = extension(name);

                        if (ext != null && ext.equals(extension)) {
                            if (sfx != null) {
                                return sfx.equals(suffix);
                            } else {
                                return true;
                            }
                        }

                        return false;
                    }
                });

            toAdd.clear();

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
                            path = base(path) + "." + extension;
                        }

                        toAdd.add(path);
                    } catch (IOException e) {
                        throw new UncheckedWrapperException
                            ("cannot get file path", e);
                    }
                }
            }

            if (suffix != null) {
                for (Iterator it = toAdd.iterator(); it.hasNext(); ) {
                    String path = (String) it.next();
                    String shadow = base(path) + "." + suffix + "." +
                        extension;
                    if (!path.equals(shadow) && toAdd.contains(shadow)) {
                        if (s_log.isDebugEnabled()) {
                            s_log.debug(
                                "Ignoring " + path +
                                " because it is shadowed by  " + shadow
                                );
                        }
                        it.remove();
                    } else if (s_log.isDebugEnabled()) {
                        s_log.debug("Found file " + path);
                    }
                }
            }

            files.addAll(toAdd);
        }
    }

    private static final String base(String path) {
        String suffix = suffix(path);
        if (suffix == null) {
            return basename(path);
        } else {
            return basename(basename(path));
        }
    }

    private static final String suffix(String path) {
        String result = extension(basename(path));
        if (SUFFIXES.contains(result)) {
            return result;
        } else {
            return null;
        }
    }

    private static final String extension(String path) {
        if (path == null) { return null; }

        int idx = path.lastIndexOf('.');
        if (idx > -1) {
            return path.substring(idx + 1);
        } else {
            return null;
        }
    }

    private static final String basename(String path) {
        int idx = path.lastIndexOf('.');
        if (idx > -1) {
            return path.substring(0, idx);
        } else {
            return path;
        }
    }


    /**
     * Compiles PDL to Persistence Metadata
     *
     * @param files array of PDL files to process
     */
    public static void compilePDLFiles(Collection files)
        throws PDLException {
        StringBuffer sb = new StringBuffer();
        PDL pdl = new PDL();

        for (Iterator it = files.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            try {
                pdl.load(file);
            } catch (PDLException e) {
                sb.append(file).append(": ");
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
