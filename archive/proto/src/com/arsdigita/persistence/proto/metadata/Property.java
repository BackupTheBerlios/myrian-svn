package com.arsdigita.persistence.proto.metadata;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2003/01/28 $
 **/

public abstract class Property extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Property.java#4 $ by $Author: rhs $, $DateTime: 2003/01/28 19:17:39 $";


    public static abstract class Switch {
        public abstract void onRole(Role role);
        public abstract void onAlias(Alias alias);
        public abstract void onLink(Link link);
    }


    private String m_name;

    protected Property(String name) {
        m_name = name;
    }

    public ObjectType getContainer() {
        return (ObjectType) getParent();
    }

    public String getName() {
        return m_name;
    }

    public abstract ObjectType getType();

    public abstract boolean isNullable();

    public abstract boolean isCollection();

    public abstract boolean isComponent();

    public abstract void dispatch(Switch sw);

    Object getKey() {
        return m_name;
    }

}
