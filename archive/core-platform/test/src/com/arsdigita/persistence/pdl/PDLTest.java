/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.MetadataRoot;
import junit.framework.TestCase;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * PDLTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/03/30 $
 */

public class PDLTest extends TestCase {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/persistence/pdl/PDLTest.java#8 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private static Logger s_log =
        Logger.getLogger(PDLTest.class.getName());

    static final String PDL_FILE_ROOT_DIR = "com/arsdigita/persistence/pdl/";
    // This is a comma delimited format containing the names of bad PDL test files,
    // along with an optional error strings that should be in the exception thrown
    // by the PDL parser. The file name should not contain the .pdl extension. Ex:
    // foo,BAD
    // bar,Bad,Type2
    // gronk
    static final String BAD_PDL_FILE_INDEX = PDL_FILE_ROOT_DIR + "badpdl.csv";

    public PDLTest(String name) {
        super(name);
    }

    public void test() {
        MetadataRoot r = MetadataRoot.getMetadataRoot();
        PDL m = new PDL();
        try
            {
                m.loadResource("com/arsdigita/persistence/pdl/test1.pdl");
                m.loadResource("com/arsdigita/persistence/pdl/test2.pdl");
            }
        catch(PDLException p)
            {
                fail("Error loading pdl files. Should not fail. " + p.getMessage());
            }


        //s_log.debug(m.getAST());

        try
            {
                m.generateMetadata(r);
                //s_log.debug(r);

            } catch(Exception me)
                {
                    fail("Error generating meta data. Should not fail. " + me.getMessage());
                }

        try
            {
                PDL bad = new PDL();
                bad.loadResource("com/arsdigita/persistence/pdl/bad1.pdl");
                fail("PDL Failed to throw an exception on bad1.pdl!");
            }
        catch(PDLException p)
            {
                s_log.debug(p);
            }
    }

    // this should fail now due to object type/super type check
    public void FAILStestEmptyObject() throws Exception {
        getPDL("emptyObject").generateMetadata(MetadataRoot.getMetadataRoot());
    }

    private String makeResourceName(String baseName) {
        return PDL_FILE_ROOT_DIR + baseName + ".pdl";
    }

    private PDL getPDL(String baseName) throws Exception {
        PDL pdl = new PDL();
        pdl.loadResource(makeResourceName(baseName));
        return pdl;
    }

    /*  Loads the given PDL file, parses it, and checks to see if there are
     *  any errors. If there is an error, it must also contain any error
     *  codes in errorKeywords.
     *
     */
    private void checkErrorMessage(String basePDLName,
                                   Collection errorKeywords,
                                   Collection failures)
        throws Exception
    {
        s_log.info("Checking bad pdl: " + basePDLName);
        try {
            MetadataRoot r = MetadataRoot.getMetadataRoot();
            PDL pdl = getPDL(basePDLName);
            pdl.generateMetadata(r);
        } catch (Throwable e) {
            //s_log.info(basePDLName + " error " + e.getMessage());
            String s = e.toString();
            Iterator iter = errorKeywords.iterator();
            while(iter.hasNext())  {
                final String errorString = (String)iter.next();
                if (s.indexOf(errorString) < 0) {
                    failures.add("error message for " + basePDLName +
                                 " does not contain \"" + errorString
                                 + "\"; error message:\n" + s);
                }
            }
            return;
        }

        // This can't be in the try clause because then it would
        // be caught.
        failures.add("no exception for " + basePDLName);
    }

    /**
     *  This test method imports a list of PDL files contained in BAD_PDL_FILE_INDEX.
     *  Each PDL file is then loaded and evaluated. The files all contain errors,
     *  and the PDL processor should catch them. Each test in BAD_PDL_FILE_INDEX also
     *  has 0..N error messages that should be in the exception thrown by the parser.
     *  The test fails if no error is detected, or if the error does not report the
     *  correct information.
     */
    public void testBadPDLFiles() throws Exception {
        // File input reader for the list of bad pdl files.
        // Using LineNumberReader for convenience in reporting problems in
        // the file
        LineNumberReader reader = null;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(BAD_PDL_FILE_INDEX);
            reader = new LineNumberReader(new InputStreamReader(is));

            LinkedList failures = new LinkedList();
            String line = null;
            while( (line = reader.readLine()) != null ) {
                final boolean lineIsCommented = line.startsWith("//");
                if( lineIsCommented ) {
                    continue;
                }
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                final boolean formatIsBad = (tokenizer.countTokens() < 1);
                if (formatIsBad) {
                    s_log.info("Warning: Incorrect format at line " + reader.getLineNumber()
                               + ". Line will not be parsed. Value is: " + line);
                } else {
                    final String fileName = tokenizer.nextToken().trim();
                    // optional list of error keywords that should be in an error message.
                    LinkedList errorKeywords = new LinkedList();
                    while (tokenizer.hasMoreElements()) {
                        errorKeywords.add(tokenizer.nextToken().trim());
                    }
                    checkErrorMessage( fileName, Collections.unmodifiableList(errorKeywords), failures );
                }
            }
            if( failures.size() > 0 ) {
                fail("Bad PDL Files parsed correctly! " + failures);
            }
        } finally {
            if( null != reader ) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public void testIncrementalLoad() throws Exception {
        try {
            MetadataRoot root = MetadataRoot.getMetadataRoot();
            PDL pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart1.pdl"
                             );
            pdl.generateMetadata(root);

            pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart2.pdl"
                             );
            pdl.generateMetadata(root);

            pdl = new PDL();
            pdl.loadResource(
                             "com/arsdigita/persistence/pdl/IncrementalLoadPart3.pdl"
                             );
            pdl.generateMetadata(root);

            //root.getModel("incrementalLoad").outputPDL(System.out);
            //root.getModel("incrementalLoadOtherModel").outputPDL(System.out);
        } catch (PDLException e) {
            fail("Incremental load failed: " + e.getMessage());
        }
    }

    public void testMultipleStatementsInPropertyAdd() throws Exception {
        PDL pdl = getPDL("MultipleStatementsInPropertyAdd");
        pdl.generateMetadata(MetadataRoot.getMetadataRoot());
    }

}
