package com.arsdigita.persistence.pdl;

import com.arsdigita.util.*;

import java.io.*;
import java.util.zip.*;

/**
 * ZipSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/09/12 $
 **/

public class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/ZipSource.java#3 $ by $Author: rhs $, $DateTime: 2003/09/12 10:06:13 $";

    private final ZipInputStream m_zis;
    private final PDLFilter m_filter;

    public ZipSource(ZipInputStream zis, PDLFilter filter) {
        m_zis = zis;
        m_filter = filter;
    }

    public void parse(PDLCompiler compiler) {
        while (true) {
            try {
                final ZipEntry entry = m_zis.getNextEntry();
                if (entry == null) { break; }
                String name = entry.getName();
                if (entry.isDirectory() ||
                    !m_filter.accept(name)) { continue; }
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
