package com.arsdigita.persistence.proto.metadata;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/02 $
 **/

public abstract class Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Property.java#2 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";


    public static abstract class Switch {
        public abstract void onRole(Role role);
        public abstract void onAlias(Alias alias);
        public abstract void onLink(Link link);
    }


    private ObjectType m_container = null;
    private String m_name;

    protected Property(String name) {
        m_name = name;
    }

    void setContainer(ObjectType container) {
        m_container = container;
    }

    public ObjectType getContainer() {
        return m_container;
    }

    public String getName() {
        return m_name;
    }

    public abstract ObjectType getType();

    public abstract boolean isCollection();

    public abstract boolean isComponent();

    public abstract void dispatch(Switch sw);

}
