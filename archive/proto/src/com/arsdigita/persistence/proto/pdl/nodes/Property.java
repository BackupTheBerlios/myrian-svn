package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/02 $
 **/

public class Property extends Statement {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Property.java#2 $ by $Author: rhs $, $DateTime: 2003/01/02 15:38:03 $";

    public static final Field TYPE =
        new Field(Property.class, "type", Type.class, 1, 1);
    public static final Field NAME =
        new Field(Property.class, "name", Identifier.class, 1, 1);
    public static final Field MAPPING =
        new Field(Property.class, "mapping", Node.class, 1, 1);

    private boolean m_isUnique = false;
    private boolean m_isComponent = false;
    private boolean m_isComposite = false;
    private boolean m_isCollection = false;

    public void setUnique(boolean b) {
        m_isUnique = b;
    }

    public void setComponent(boolean b) {
        m_isComponent = b;
    }

    public void setComposite(boolean b) {
        m_isComposite = b;
    }

    public void setCollection(boolean b) {
        m_isCollection = b;
    }

    public boolean isComponent() {
        return m_isComponent;
    }

    public boolean isComposite() {
        return m_isComposite;
    }

    public boolean isCollection() {
        return m_isCollection;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onProperty(this);
    }

    public Identifier getName() {
        return (Identifier) get(NAME);
    }

    public Type getType() {
        return (Type) get(TYPE);
    }

}
