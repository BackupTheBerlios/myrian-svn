package com.arsdigita.persistence.proto;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/04 $
 **/

public abstract class InFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/InFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

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

    public String toString() {
        return m_path + " in (" + m_query + ")";
    }
}
