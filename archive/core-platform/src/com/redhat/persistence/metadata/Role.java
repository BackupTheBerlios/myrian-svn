package com.redhat.persistence.metadata;

/**
 * Role
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class Role extends Property {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Role.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    private ObjectType m_type;
    private Role m_reverse;
    private boolean m_isComponent;
    private boolean m_isCollection;
    private boolean m_isNullable;

    public Role(String name, ObjectType type, boolean isComponent,
                boolean isCollection, boolean isNullable) {
        super(name);
        m_type = type;
        m_isComponent = isComponent;
        m_isCollection = isCollection;
        m_isNullable = isNullable;
    }

    public ObjectType getType() {
        return m_type;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_reverse != null && m_reverse.isComponent();
    }

    public boolean isNullable() {
        return m_isNullable;
    }

    public void setNullable(boolean value) {
        m_isNullable = value;
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public boolean isReversable() {
        return m_reverse != null;
    }

    public Role getReverse() {
        return m_reverse;
    }

    public void setReverse(Role reverse) {
        if (reverse == null) {
            throw new IllegalArgumentException
                ("reverse is null");
        }
        if (reverse.m_reverse != null) {
            throw new IllegalArgumentException
                ("Role is associated with another property already: " +
                 reverse);
        }

        this.m_reverse = reverse;
        reverse.m_reverse = this;
    }

    public void dispatch(Switch sw) {
        sw.onRole(this);
    }

}
