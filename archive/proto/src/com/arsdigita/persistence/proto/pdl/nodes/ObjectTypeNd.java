package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2003/03/15 $
 **/

public class ObjectTypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ObjectTypeNd.java#6 $ by $Author: rhs $, $DateTime: 2003/03/15 02:35:11 $";

    public static final Field VERSIONED =
        new Field(ObjectTypeNd.class, "versioned", VersionedNd.class, 0, 1);
    public static final Field NAME =
        new Field(ObjectTypeNd.class, "name", IdentifierNd.class, 1, 1);
    public static final Field EXTENDS =
        new Field(ObjectTypeNd.class, "extends", TypeNd.class, 0, 1);
    public static final Field ADAPTER =
        new Field(ObjectTypeNd.class, "adapter", JavaClassNd.class, 0, 1);
    public static final Field PROPERTIES =
        new Field(ObjectTypeNd.class, "properties", PropertyNd.class);
    public static final Field OBJECT_KEY =
        new Field(ObjectTypeNd.class, "objectKey", ObjectKeyNd.class, 0, 1);
    public static final Field REFERENCE_KEY =
        new Field(ObjectTypeNd.class, "referenceKey", ReferenceKeyNd.class, 0,
                  1);
    public static final Field UNIQUE_KEYS =
        new Field(ObjectTypeNd.class, "uniqueKeys", UniqueKeyNd.class);
    public static final Field AGGRESSIVE_LOAD =
        new Field(ObjectTypeNd.class, "aggressiveLoad",
                  AggressiveLoadNd.class);
    public static final Field JOIN_PATHS =
        new Field(ObjectTypeNd.class, "joinPaths", JoinPathNd.class);
    public static final Field EVENTS =
        new Field(ObjectTypeNd.class, "events", EventNd.class);


    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onObjectType(this);
    }

    public String getQualifiedName() {
        return getFile().getModel().getName() + "." + getName().getName();
    }

    public IdentifierNd getName() {
        return (IdentifierNd) get(NAME);
    }

    public VersionedNd getVersioned() {
        return (VersionedNd) get(VERSIONED);
    }

    public TypeNd getExtends() {
        return (TypeNd) get(EXTENDS);
    }

    public ReferenceKeyNd getReferenceKey() {
        return (ReferenceKeyNd) get(REFERENCE_KEY);
    }

    public ObjectKeyNd getObjectKey() {
        return (ObjectKeyNd) get(OBJECT_KEY);
    }

    private boolean m_returns = false;

    public boolean hasReturns() {
        return m_returns;
    }

    public void setReturns(boolean value) {
        m_returns = value;
    }

}
