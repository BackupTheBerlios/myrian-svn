package com.arsdigita.persistence.pdl;

import com.arsdigita.util.*;

import java.io.*;
import java.util.zip.*;

/**
 * ZipSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/11 $
 **/

public class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/ZipSource.java#1 $ by $Author: rhs $, $DateTime: 2003/09/11 14:54:54 $";

    private final ZipInputStream m_zis;

    public ZipSource(ZipInputStream zis) {
        m_zis = zis;
    }

    public void parse(PDLCompiler compiler) {
        while (true) {
            try {
                final ZipEntry entry = m_zis.getNextEntry();
                if (entry == null) { break; }
                if (entry.isDirectory()) { continue; }
                compiler.parse(new InputStreamReader(m_zis), entry.getName());
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }

}
