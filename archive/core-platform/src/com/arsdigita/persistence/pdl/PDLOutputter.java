package com.arsdigita.persistence.pdl;

import com.arsdigita.persistence.metadata.*;

import java.util.Collection;
import java.util.Iterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * An class that outputs the PDL associated with a particular
 * metadata set to files.  Each file will contain the metadata for a single
 * model.  The output will go to the a given directory, with each PDL
 * file being named after the fully qualified model name.
 *
 * @author <a href="mailto:pmcneill@arsdigita.com">Patrick McNeill</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/PDLOutputter.java#1 $
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
