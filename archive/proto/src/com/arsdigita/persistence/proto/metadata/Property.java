package com.arsdigita.persistence.proto.metadata;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public abstract class Property {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/metadata/Property.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

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

}
