package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * An implementation of {@link PDLSource} that loads the contents of a
 * zip or jar file. This class doesn't take a filter at the moment
 * because PDL loading needs a manifest to decide whether or not to
 * load foo.pdl, because foo.<db>.pdl may be present.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/01/16 $
 **/

class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ZipSource.java#4 $ by $Author: ashah $, $DateTime: 2004/01/16 13:01:04 $";

    private final ZipInputStream m_zis;

    /**
     * @param zis the ZipInputStream to load
     **/

    public ZipSource(ZipInputStream zis) {
        m_zis = zis;
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
                if (entry.isDirectory()) { continue; }
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
