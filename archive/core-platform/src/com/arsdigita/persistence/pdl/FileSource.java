package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * FileSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/10/28 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/FileSource.java#2 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private final File m_file;

    public FileSource(File file) {
        m_file = file;
    }

    public FileSource(String filename) {
        this(new File(filename));
    }

    public void parse(PDLCompiler compiler) {
        try {
            compiler.parse(new FileReader(m_file), m_file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
