package com.arsdigita.persistence.pdl;

import java.io.*;

/**
 * DirSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/11 $
 **/

public class DirSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/DirSource.java#1 $ by $Author: rhs $, $DateTime: 2003/09/11 14:54:54 $";

    private final File m_dir;

    public DirSource(File dir) {
        m_dir = dir;
    }

    public void parse(PDLCompiler compiler) {
        // TODO: implement
    }

}
