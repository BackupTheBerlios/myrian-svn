package com.arsdigita.persistence.proto;

/**
 * ContainsFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/04 $
 **/

public abstract class ContainsFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/ContainsFilter.java#1 $ by $Author: rhs $, $DateTime: 2002/12/04 19:18:22 $";

    private Path m_path;
    private Object m_value;

    protected ContainsFilter(Path path, Object value) {
        m_path = path;
        m_value = value;
    }

    public Path getPath() {
        return m_path;
    }

    public Object getValue() {
        return m_value;
    }

    public String toString() {
        return m_path + ".contains(" + m_value +")";
    }
}
