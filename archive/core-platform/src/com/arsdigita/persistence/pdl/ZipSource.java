/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
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
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ZipSource.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
