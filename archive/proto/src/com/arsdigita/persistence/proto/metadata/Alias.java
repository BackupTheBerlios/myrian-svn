package com.arsdigita.persistence.proto.metadata;

/**
 * Alias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/01/28 $
 **/

public class Alias extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Alias.java#3 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";

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

    public boolean isNullable() {
        return m_target.isNullable();
    }

    public boolean isCollection() {
        return m_target.isCollection();
    }

    public boolean isComponent() {
        return m_target.isComponent();
    }

    public void dispatch(Switch sw) {
        sw.onAlias(this);
    }

}
