package com.redhat.persistence.metadata;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public abstract class Property extends Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Property.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";


    public static abstract class Switch {
        public abstract void onRole(Role role);
        public abstract void onAlias(Alias alias);
        public abstract void onLink(Link link);
    }


    private String m_name;

    protected Property(String name) {
        m_name = name;
    }

    public Root getRoot() {
	return getContainer().getRoot();
    }

    public ObjectType getContainer() {
        return (ObjectType) getParent();
    }

    public String getName() {
        return m_name;
    }

    public boolean isKeyProperty() {
	return getContainer().isKeyProperty(this);
    }

    public abstract ObjectType getType();

    public abstract boolean isNullable();

    public abstract boolean isCollection();

    public abstract boolean isComponent();

    public abstract boolean isComposite();

    public abstract void dispatch(Switch sw);

    Object getElementKey() {
        return m_name;
    }

    public String toString() {
        return getName();
    }
}
