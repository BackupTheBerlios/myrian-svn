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
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Comparator;

/**
 * An implementation of PDLSource that loads all files under a
 * directory.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/03/30 $
 **/

public class DirSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/DirSource.java#6 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private final File m_dir;
    private final PDLFilter m_filter;

    /**
     * Constructs a new PDLSource that loads all files under
     * <code>dir</code> that meet the criteria specified by
     * <code>filter</code>.
     *
     * @param dir the base directory
     * @param filter the PDLFilter used to restrict results
     **/

    public DirSource(File dir, PDLFilter filter) {
        m_dir = dir;
        m_filter = filter;
    }

    /**
     * Parses the contents of this PDLSource using the given PDLCompiler.
     *
     * @param compiler the PDLCompiler used to parse this source
     **/

    public void parse(PDLCompiler compiler) {
        parse(compiler, m_dir);
    }

    private void parse(PDLCompiler compiler, File dir) {
        File[] listing = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        });

        Arrays.sort(listing, FILE_NAME);

        ArrayList names = new ArrayList();

        for (int i = 0; i < listing.length; i++) {
            names.add(listing[i].getAbsolutePath());
        }

        Collection accepted = m_filter.accept(names);

        for (int i = 0; i < listing.length; i++) {
            File file = listing[i];
            if (accepted.contains(file.getAbsolutePath())) {
                try {
                    FileReader reader = new FileReader(file);
                    try {
                        compiler.parse(reader, file.getAbsolutePath());
                    } finally {
                        reader.close();
                    }
                } catch (IOException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }

        File[] subdirs = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        Arrays.sort(subdirs, FILE_NAME);

        for (int i = 0; i < subdirs.length; i++) {
             parse(compiler, subdirs[i]);
        }
    }

    private static final Comparator FILE_NAME = new Comparator() {
        public int compare(Object o1, Object o2) {
            File f1 = (File) o1;
            File f2 = (File) o2;
            return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
        }
    };

}
