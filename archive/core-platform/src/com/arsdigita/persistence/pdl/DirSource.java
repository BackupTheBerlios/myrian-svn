package com.arsdigita.persistence.pdl;

import com.arsdigita.util.UncheckedWrapperException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * DirSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/10/28 $
 **/

public class DirSource implements PDLSource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/DirSource.java#2 $ by $Author: jorris $, $DateTime: 2003/10/28 18:36:21 $";

    private final File m_dir;
    private final PDLFilter m_filter;

    public DirSource(File dir, PDLFilter filter) {
        m_dir = dir;
        m_filter = filter;
    }

    public void parse(PDLCompiler compiler) {
        parse(compiler, m_dir);
    }

    private void parse(PDLCompiler compiler, File dir) {
        File[] listing = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory()
                    || m_filter.accept(file.getAbsolutePath());
            }
        });

        Arrays.sort(listing, FILE_NAME);

        for (int i = 0; i < listing.length; i++) {
            File file = listing[i];
            if (file.isDirectory()) {
                parse(compiler, file);
            } else {
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
    }

    private static final Comparator FILE_NAME = new Comparator() {
        public int compare(Object o1, Object o2) {
            File f1 = (File) o1;
            File f2 = (File) o2;
            return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
        }
    };

}
