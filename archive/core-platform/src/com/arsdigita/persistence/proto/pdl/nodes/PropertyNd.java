package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Property
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2003/06/02 $
 **/

public class PropertyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/PropertyNd.java#3 $ by $Author: rhs $, $DateTime: 2003/06/02 10:49:07 $";

    public static final Field TYPE =
        new Field(PropertyNd.class, "type", TypeNd.class, 1, 1);
    public static final Field NAME =
        new Field(PropertyNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field MAPPING =
        new Field(PropertyNd.class, "mapping", Node.class, 1, 1);

    private boolean m_isImmediate = false;
    private boolean m_isUnique = false;
    private boolean m_isComponent = false;
    private boolean m_isComposite = false;
    private boolean m_isCollection = false;
    private boolean m_isNullable = true;

    private boolean m_isUnversioned = false;
    private boolean m_isVersioned = false;

    /**
     * @post isImmediate()
     **/
    public void setImmediate() {
        m_isImmediate = true;
    }

    public boolean isImmediate() {
        return m_isImmediate;
    }

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
     * @see #isVersioned()
     * @post isVersioned()
     **/
    public void setVersioned() {
        m_isVersioned = true;
    }

    /**
     * @see #isUnversioned()
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

    /**
     * Returns <code>true</code> if this property is marked "versioned" in the
     * PDL.
     *
     * @see #isUnversioned()
     * @see #setVersioned()
     **/
    public boolean isVersioned() {
        return m_isVersioned;
    }

    /**
     * Returns <code>true</code> if this property is marked "unversioned" in the PDL.
     * 
     * <p>Note that <code>isUnversioned()</code> is <em>not</em> the same as
     * <code>!isVersioned()</code>. If the property is marked neither
     * "versioned", nor "unversioned" (as most properties are), then both of
     * these methods return <code>false</code>. However, if one of them returns
     * <code>true</code>, then other returns <code>false</code>. </p>
     *
     * @see #isVersioned()
     * @see #setVersioned()
     **/
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
