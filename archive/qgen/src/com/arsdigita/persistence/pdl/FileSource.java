package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * An implementation of PDLSource that loads an individual file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/FileSource.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private final File m_file;

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param file the PDL file
     **/

    public FileSource(File file) {
        m_file = file;
    }

    /**
     * Constructs a PDLSource with the contents of the given file.
     *
     * @param filename the name of the PDL file
     **/

    public FileSource(String filename) {
        this(new File(filename));
    }

    /**
     * Parses the contents of this PDLSource using the given compiler.
     *
     * @param compiler the PDLCompiler used to parse this PDLSource
     **/

    public void parse(PDLCompiler compiler) {
        try {
            compiler.parse(new FileReader(m_file), m_file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
