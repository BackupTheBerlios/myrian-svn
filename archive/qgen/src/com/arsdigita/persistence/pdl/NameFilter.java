package com.arsdigita.persistence.pdl;

import java.io.File;

/**
 * An implementation of {@link PDLFilter} that filters based on
 * extension and suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class NameFilter implements PDLFilter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/NameFilter.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private final String m_suffix;
    private final String m_extension;

    /**
     * Constructs a name filter that only accepts pdl files with the
     * given suffix and extension.
     *
     * @param suffix the allowed suffix
     * @param extension the allowed extension
     **/

    public NameFilter(String suffix, String extension) {
        m_suffix = suffix;
        m_extension = extension;
    }

    /**
     * Tests <code>name</code> against this NameFilters suffix and
     * extension.
     *
     * @param name the name to test
     **/

    public boolean accept(String name) {
        String base;

        int idx = name.lastIndexOf('.');
        String ext;
        if (idx > -1) {
            ext = name.substring(idx + 1);
            base = name.substring(0, idx);
            if (!ext.equals(m_extension)) { return false; }
        } else {
            return false;
        }

        idx = base.lastIndexOf('.');
        int idx2 = base.lastIndexOf(File.separatorChar);
        String sfx;
        if (idx > -1 && idx > idx2) {
            sfx = base.substring(idx + 1);
            return sfx.equals(m_suffix);
        } else {
            return true;
        }
    }

}
