package com.arsdigita.persistence.proto.metadata;

/**
 * Alias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Alias extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Alias.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    private Property m_target;

    public Alias(String name, Property target) {
        super(name);
        m_target = target;
    }

    public Property getTarget() {
        return m_target;
    }

    public ObjectType getType() {
        return m_target.getType();
    }

}
