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

package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.pdl.ast.AST;
import com.arsdigita.persistence.Utilities;
import com.arsdigita.persistence.metadata.MetadataRoot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

/**
 * The main class that is used to process PDL files.  It takes any number of 
 * PDL files as arguments on the command line, then processes them all into 
 * a single XML file (the first command line argument).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

public class PDL {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDL.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    private static final Category s_log = Category.getInstance(PDL.class);

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

        if (args.size() > 0 && "-debugDirectory".equals(args.get(0))) {
            if (args.size() > 1) {
                setDebugDirectory((String) args.get(1));
                for (int i = 2; i < args.size(); i++) {
                    args.set(i - 2, args.get(i));
                }
                args.remove(args.size() - 1);
                args.remove(args.size() - 1);
            } else {
                usage();
            }
        }

        if (args.size() < 1) {
            usage();
        } else {
            BasicConfigurator.configure();
            Category.getRoot().setPriority(Priority.toPriority("info"));

            compilePDL(args);
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

    /**
     * Compiles PDL to Persistence Metadata
     *
     * @param files array of PDL files to process
     */
    public static void compilePDL(List files)
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
                    // Future use -- Patrick
                    PDLOutputter.writePDL(MetadataRoot.getMetadataRoot(),
                                          new java.io.File(s_debugDirectory));
                } catch (java.io.IOException e) {
                    s_log.error(
                        "There was a problem generating debugging output",
                        e
                        );
                }
            }
        } else {
            throw new PDLException(sb.toString());
        }
    }
}
