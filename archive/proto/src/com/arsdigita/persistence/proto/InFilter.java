package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2003/01/15 $
 **/

public class InFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/InFilter.java#5 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Path m_path;
    private Query m_query;

    protected InFilter(Path path, Query query) {
        m_path = path;
        m_query = query;
    }

    public Path getPath() {
        return m_path;
    }

    public Query getQuery() {
        return m_query;
    }

    public void dispatch(Switch sw) {
        sw.onIn(this);
    }

    public String toString() {
        return m_path + " in (" + m_query + ")";
    }
}
