package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * The ManifestSource class provides an implementation of the {@link
 * PDLSource} interface that loads object-relational metadata from a
 * manifest file that lists resources located in the java classpath.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/11/06 $
 **/

public class ManifestSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ManifestSource.java#3 $ by $Author: rhs $, $DateTime: 2003/11/06 00:02:45 $";

    private final String m_manifest;
    private final PDLFilter m_filter;
    private final ClassLoader m_loader;

    /**
     * Constructs a new ManifestSource from the resources listed in
     * <code>manifest</code>. This source will be filtered by
     * <code>filter</code>, and <code>loader</code> will be used to
     * load all resources.
     *
     * @param manifest a resource path referring to a manifest file
     * @param filter a filter on the names in the manifest file
     * @param loader the loader used to locate resources
     **/

    public ManifestSource(String manifest, PDLFilter filter,
                          ClassLoader loader) {
        m_manifest = manifest;
        m_filter = filter;
        m_loader = loader;
    }

    /**
     * Invokes {@link #ManifestSource(String, PDLFilter, ClassLoader)}
     * with the current context class loader.
     *
     * @param manifest a resource path referring to a manifest file
     * @param filter a filter on the names in the manifest file
     *
     * @see Thread#getContextClassLoader()
     **/

    public ManifestSource(String manifest, PDLFilter filter) {
        this(manifest, filter, Thread.currentThread().getContextClassLoader());
    }

    /**
     * An implementation of {@link PDLSource#parse(PDLCompiler)} that
     * parses the resource listed in the manifest passed to the
     * constructor of this ManifestSource.
     *
     * @param compiler the compiler used to parse
     **/

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
