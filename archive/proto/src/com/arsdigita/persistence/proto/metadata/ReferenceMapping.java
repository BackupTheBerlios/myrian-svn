package com.arsdigita.persistence.proto.metadata;

import com.arsdigita.persistence.proto.common.*;

import java.util.*;

/**
 * ReferenceMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/15 $
 **/

public class ReferenceMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ReferenceMapping.java#2 $ by $Author: rhs $, $DateTime: 2003/01/15 16:58:00 $";

    private ArrayList m_joins = new ArrayList();

    public ReferenceMapping(Path path) {
        super(path);
    }

    public Collection getJoins() {
        return m_joins;
    }

    public void addJoin(Join join) {
        m_joins.add(join);
    }

}
