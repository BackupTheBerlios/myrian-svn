package com.arsdigita.persistence.pdl;

import com.arsdigita.util.*;

import java.io.*;

/**
 * FileSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/11 $
 **/

public class FileSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/FileSource.java#1 $ by $Author: rhs $, $DateTime: 2003/09/11 14:54:54 $";

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
