package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * PassthroughFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/04/24 $
 **/

public class PassthroughFilter extends Filter {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/PassthroughFilter.java#2 $ by $Author: rhs $, $DateTime: 2003/04/24 08:07:11 $";

    private String m_conditions;

    public PassthroughFilter(String conditions) {
        m_conditions = conditions;
    }

    public String getConditions() {
        return m_conditions;
    }

    public void dispatch(Switch sw) {
        sw.onPassthrough(this);
    }

    public String toString() {
        return m_conditions;
    }

}
