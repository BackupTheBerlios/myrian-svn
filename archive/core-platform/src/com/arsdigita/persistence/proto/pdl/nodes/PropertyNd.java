package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class PropertyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/PropertyNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

    public static final Field TYPE =
        new Field(PropertyNd.class, "type", TypeNd.class, 1, 1);
    public static final Field NAME =
        new Field(PropertyNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(PropertyNd.class, "mapping", Node.class, 1, 1);

    private boolean m_isUnique = false;
    private boolean m_isComponent = false;
    private boolean m_isComposite = false;
    private boolean m_isCollection = false;
    private boolean m_isNullable = true;
    private boolean m_isUnversioned = false;

    /**
     * @post isUnique()
     **/
    public void setUnique() {
        m_isUnique = true;
    }

    /**
     * @post isComponent()
     **/
    public void setComponent() {
        m_isComponent = true;
    }

    /**
     * @post isComposite()
     **/
    public void setComposite() {
        m_isComposite = true;
    }

    /**
     * @post isCollection()
     **/
    public void setCollection() {
        m_isCollection = true;
    }

    public void setNullable(boolean b) {
        m_isNullable = b;
    }

    /**
     * @post isUnversioned()
     **/
    public void setUnversioned() {
        m_isUnversioned = true;
    }
    public boolean isUnique() {
        return m_isUnique;
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

    public boolean isNullable() {
        return m_isNullable;
    }

    public boolean isUnversioned() {
        return m_isUnversioned;
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onProperty(this);
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public TypeNd getType() {
        return (TypeNd) get(TYPE);
    }

    public Node getMapping() {
        return (Node) get(MAPPING);
    }
}
