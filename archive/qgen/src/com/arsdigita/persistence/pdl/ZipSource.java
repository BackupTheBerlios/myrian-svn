package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * An implementation of {@link PDLSource} that loads the contents of a
 * zip or jar file.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/29 $
 **/

class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/ZipSource.java#2 $ by $Author: ashah $, $DateTime: 2004/01/29 12:35:08 $";

    private final ZipFile m_file;
    private final PDLFilter m_filter;

    /**
     * @param zis the ZipInputStream to load
     **/

    public ZipSource(ZipFile file, PDLFilter filter) {
        m_file = file;
        m_filter = filter;
    }

    /**
     * Parses the contents of this PDLSource using the given
     * PDLCompiler.
     *
     * @param compiler the compiler used to parse this PDLSource
     **/

    public void parse(PDLCompiler compiler) {
        Enumeration entries = m_file.entries();
        HashSet entrynames = new HashSet();
        while (entries.hasMoreElements()) {
            entrynames.add(((ZipEntry)entries.nextElement()).getName());
        }
        Collection accepted = m_filter.accept(entrynames);

        Iterator iter = accepted.iterator();
        while (iter.hasNext()) {
            try {
                String entryname = (String)iter.next();
                ZipEntry entry = new ZipEntry (entryname);
                if (entry.isDirectory()) { continue; }
                String name = entry.getName();
                compiler.parse(new InputStreamReader(m_file.getInputStream(entry)) {
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
            } catch (IllegalStateException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }
}
