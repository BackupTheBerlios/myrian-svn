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
 * @version $Revision: #4 $ $Date: 2004/01/16 $
 **/

public class DirSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/DirSource.java#4 $ by $Author: ashah $, $DateTime: 2004/01/16 13:01:04 $";

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
            names.add(listing[i].getName());
        }

        Collection accepted = m_filter.accept(names);

        for (int i = 0; i < listing.length; i++) {
            File file = listing[i];
            if (accepted.contains(file.getName())) {
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
