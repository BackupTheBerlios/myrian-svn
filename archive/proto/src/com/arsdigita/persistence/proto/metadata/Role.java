package com.arsdigita.persistence.proto.metadata;

/**
 * Role
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Role extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Role.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private ObjectType m_type;

    public Role(String name, ObjectType type) {
        super(name);
        m_type = type;
    }

    public ObjectType getType() {
        return m_type;
    }

}
