package com.arsdigita.persistence.proto;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/06 $
 **/

public abstract class InFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/InFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/01/06 16:31:02 $";

    private Path m_path;
    private Binding m_binding;

    protected InFilter(Path path, Binding binding) {
        m_path = path;
        m_binding = binding;
    }

    public Path getPath() {
        return m_path;
    }

    public Binding getBinding() {
        return m_binding;
    }

    public String toString() {
        return m_path + " in (" + m_binding + ")";
    }
}
