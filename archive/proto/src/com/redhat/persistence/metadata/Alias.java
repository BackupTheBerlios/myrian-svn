package com.redhat.persistence.metadata;

/**
 * Alias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/08/04 $
 **/

public class Alias extends Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/redhat/persistence/metadata/Alias.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

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

    public boolean isComposite() {
        return m_target.isComposite();
    }

    public void dispatch(Switch sw) {
        sw.onAlias(this);
    }

}
