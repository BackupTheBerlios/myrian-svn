/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The ManifestSource class provides an implementation of the {@link
 * PDLSource} interface that loads object-relational metadata from a
 * manifest file that lists resources located in the java classpath.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/05/03 $
 **/

public class ManifestSource implements PDLSource {

    public final static String versionId = "$Id: //users/rhs/persistence/cap/src/com/arsdigita/persistence/pdl/ManifestSource.java#1 $ by $Author: rhs $, $DateTime: 2004/05/03 11:00:53 $";

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

            ArrayList names = new ArrayList();

            while (true) {
                String line = lines.readLine();
                if (line == null) { break; }
                line = line.trim();
                names.add(line);
            }

            for (Iterator accepted = m_filter.accept(names).iterator();
                 accepted.hasNext(); ) {

                String line = (String) accepted.next();

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
        } finally {
            try { is.close(); }
            catch (IOException e) { throw new UncheckedWrapperException(e); }
        }
    }

}
