package com.arsdigita.persistence.pdl;

/**
 * NameFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/09/12 $
 **/

public class NameFilter implements PDLFilter {

    public final static String versionId = "$Id: //core-platform/test-packaging/src/com/arsdigita/persistence/pdl/NameFilter.java#1 $ by $Author: rhs $, $DateTime: 2003/09/12 10:06:13 $";

    private final String m_suffix;
    private final String m_extension;

    public NameFilter(String suffix, String extension) {
        m_suffix = suffix;
        m_extension = extension;
    }

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
        String sfx;
        if (idx > -1) {
            sfx = base.substring(idx + 1);
            return sfx.equals(m_suffix);
        } else {
            return true;
        }
    }

}
