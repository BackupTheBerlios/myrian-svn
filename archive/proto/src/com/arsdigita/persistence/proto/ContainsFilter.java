package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

/**
 * ContainsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/15 $
 **/

public class ContainsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ContainsFilter.java#4 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private Path m_collection;
    private Path m_element;

    protected ContainsFilter(Path collection, Path element) {
        m_collection = collection;
        m_element = element;
    }

    public Path getCollection() {
        return m_collection;
    }

    public Path getElement() {
        return m_element;
    }

    public void dispatch(Switch sw) {
        sw.onContains(this);
    }

    public String toString() {
        return m_collection + ".contains(" + m_element +")";
    }

}
