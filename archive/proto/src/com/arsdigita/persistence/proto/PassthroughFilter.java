package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * PassthroughFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/28 $
 **/

public class PassthroughFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PassthroughFilter.java#1 $ by $Author: rhs $, $DateTime: 2003/02/28 17:44:25 $";

    private String m_conditions;
    private HashMap m_parameters = new HashMap();

    public PassthroughFilter(String conditions) {
        m_conditions = conditions;
    }

    public String getConditions() {
        return m_conditions;
    }

    public Collection getParameters() {
        return m_parameters.keySet();
    }

    public void setParameter(Path path, Object value) {
        m_parameters.put(path, value);
    }

    public Object getParameter(Path path) {
        return m_parameters.get(path);
    }

    public void dispatch(Switch sw) {
        sw.onPassthrough(this);
    }

    public String toString() {
        return m_conditions;
    }

}
