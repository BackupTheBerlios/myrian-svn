package com.arsdigita.persistence.pdl;

import com.arsdigita.util.*;

import java.io.*;
import java.util.zip.*;

/**
 * ZipSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/09/11 $
 **/

public class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/ZipSource.java#2 $ by $Author: rhs $, $DateTime: 2003/09/11 17:59:52 $";

    private final ZipInputStream m_zis;

    public ZipSource(ZipInputStream zis) {
        m_zis = zis;
    }

    public void parse(PDLCompiler compiler) {
        while (true) {
            try {
                final ZipEntry entry = m_zis.getNextEntry();
                if (entry == null) { break; }
                String name = entry.getName();
                if (entry.isDirectory() ||
                    !name.endsWith(".pdl")) { continue; }
                compiler.parse(new InputStreamReader(m_zis) {
                    public void close() {
                        // We need to override close here to do
                        // nothing since compiler.parse appears to
                        // close the input stream from underneath us,
                        // and that passes through to close the
                        // underlying zip input stream which is a
                        // problem when we try to read the next entry.
                    }
                }, name);
            } catch (IOException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }

}
