package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An implementation of {@link PDLSource} that loads the contents of a
 * zip or jar file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/11/06 $
 **/

public class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ZipSource.java#3 $ by $Author: rhs $, $DateTime: 2003/11/06 00:02:45 $";

    private final ZipInputStream m_zis;
    private final PDLFilter m_filter;

    /**
     * Constructs a new ZipSource with the contents given
     * ZipInputStream filtered by <code>filter</code>
     *
     * @param zis the ZipInputStream to load
     * @param filter used to filter the contents of the ZipInputStream
     **/

    public ZipSource(ZipInputStream zis, PDLFilter filter) {
        m_zis = zis;
        m_filter = filter;
    }

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the compiler used to parse this PDLSource
     **/

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
