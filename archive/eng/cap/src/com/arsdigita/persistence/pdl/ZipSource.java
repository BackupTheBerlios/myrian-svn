/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
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
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

class ZipSource implements PDLSource {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/ZipSource.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
