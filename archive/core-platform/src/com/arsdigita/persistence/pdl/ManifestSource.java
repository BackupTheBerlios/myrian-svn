package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * ManifestSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/10/28 $
 **/

public class ManifestSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ManifestSource.java#2 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private final String m_manifest;
    private final PDLFilter m_filter;
    private final ClassLoader m_loader;

    public ManifestSource(String manifest, PDLFilter filter,
                          ClassLoader loader) {
        m_manifest = manifest;
        m_filter = filter;
        m_loader = loader;
    }

    public ManifestSource(String manifest, PDLFilter filter) {
        this(manifest, filter, Thread.currentThread().getContextClassLoader());
    }

    public void parse(PDLCompiler compiler) {
        InputStream is = m_loader.getResourceAsStream(m_manifest);
        if (is == null) {
            throw new IllegalStateException("no such resource: " + m_manifest);
        }
        try {
            LineNumberReader lines =
                new LineNumberReader(new InputStreamReader(is));
            while (true) {
                try {
                    String line = lines.readLine();
                    if (line == null) { break; }
                    line = line.trim();

                    if (m_filter.accept(line)) {
                        InputStream pdl = m_loader.getResourceAsStream(line);
                        if (pdl == null) {
                            throw new IllegalStateException
                                (m_manifest + ": " + lines.getLineNumber() +
                                 ": no such resource '" + line + "'");
                        }
                        try {
                            compiler.parse(new InputStreamReader(pdl), line);
                        } finally {
                            pdl.close();
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        } finally {
            try { is.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

}
