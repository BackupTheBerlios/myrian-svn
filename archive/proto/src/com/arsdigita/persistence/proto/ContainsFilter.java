package com.arsdigita.persistence.proto;

/**
 * ContainsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/13 $
 **/

public class ContainsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ContainsFilter.java#3 $ by $Author: rhs $, $DateTime: 2003/01/13 16:40:35 $";

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
