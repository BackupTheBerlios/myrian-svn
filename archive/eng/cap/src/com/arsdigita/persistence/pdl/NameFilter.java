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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An implementation of {@link PDLFilter} that filters based on
 * extension and suffix.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class NameFilter implements PDLFilter {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/pdl/NameFilter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
