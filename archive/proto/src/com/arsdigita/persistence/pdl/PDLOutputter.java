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

import com.arsdigita.persistence.metadata.*;

import java.util.Iterator;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * An class that outputs the PDL associated with a particular
 * metadata set to files.  Each file will contain the metadata for a single
 * model.  The output will go to the a given directory, with each PDL
 * file being named after the fully qualified model name.
 *
 * @author Patrick McNeill
 * @version $Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/PDLOutputter.java#2 $
 */
public class PDLOutputter {
    /**
     * Output the contents of an entire metadata hierarchy as PDL files.
     *
     * @param root the metadata root of the hierarchy to output
     * @param directory the directory to output to
     */
    public static void writePDL(MetadataRoot root, File directory)
        throws IOException {
        Iterator models = root.getModels();

        while (models.hasNext()) {
            Model model = (Model)models.next();
            PrintStream out = new PrintStream(new FileOutputStream(new File(directory, model.getName() + ".pdl")));
            model.outputPDL(out);
            out.close();
        }
    }

}
