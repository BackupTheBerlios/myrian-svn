package com.arsdigita.persistence.pdl;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An implementation of {@link PDLFilter} that filters based on
 * extension and suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/01/29 $
 **/

public class NameFilter implements PDLFilter {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/persistence/pdl/NameFilter.java#2 $ by $Author: ashah $, $DateTime: 2004/01/29 12:35:08 $";

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
     */
    public Collection accept(Collection names) {
        // map from basename to full filename. accept the longest match.
        HashMap accepted = new HashMap();

        for (Iterator it = names.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            int idx = name.lastIndexOf('.');

            if (idx < 0) { continue; }

            String ext = name.substring(idx + 1);
            String base = name.substring(0, idx);

            if (!ext.equals(m_extension)) { continue; }

            idx = base.lastIndexOf('.');
            int idx2 = base.lastIndexOf(File.separatorChar);
            if (idx > -1 && idx > idx2) {
                String sfx = base.substring(idx + 1);

                if (!sfx.equals(m_suffix)) { continue; }

                base = base.substring(0, idx);
            }

            String cur = (String) accepted.get(base);
            if (cur == null || (cur.length() < name.length())) {
                accepted.put(base, name);
            }
        }

        return accepted.values();
    }

}
