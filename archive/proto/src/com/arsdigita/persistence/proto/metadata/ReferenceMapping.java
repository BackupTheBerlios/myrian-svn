package com.arsdigita.persistence.proto.metadata;

/**
 * ReferenceMapping
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ReferenceMapping extends Mapping {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/ReferenceMapping.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Join m_join;

    public ReferenceMapping(Path path, Join join) {
        super(path);
        m_join = join;
    }

    public Join getJoin() {
        return m_join;
    }

}
