package com.arsdigita.persistence.pdl;

import com.arsdigita.util.*;

import java.io.*;

/**
 * FileSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/10/23 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/FileSource.java#1 $ by $Author: justin $, $DateTime: 2003/10/23 15:28:18 $";

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
