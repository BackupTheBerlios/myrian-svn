package com.arsdigita.persistence.proto;

/**
 * ContainsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/06 $
 **/

public abstract class ContainsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ContainsFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

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

    public String toString() {
        return m_collection + ".contains(" + m_element +")";
    }

}
