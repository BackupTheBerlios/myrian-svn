package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ObjectType
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class ObjectType extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ObjectType.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";


    public static final Field NAME =
        new Field(ObjectType.class, "name", Identifier.class, 1, 1);
    public static final Field EXTENDS =
        new Field(ObjectType.class, "extends", Type.class, 0, 1);
    public static final Field PROPERTIES =
        new Field(ObjectType.class, "properties", Property.class);
    public static final Field OBJECT_KEY =
        new Field(ObjectType.class, "objectKey", ObjectKey.class, 0, 1);
    public static final Field REFERENCE_KEY =
        new Field(ObjectType.class, "referenceKey", ReferenceKey.class, 0, 1);
    public static final Field UNIQUE_KEYS =
        new Field(ObjectType.class, "uniqueKeys", UniqueKey.class);
    public static final Field AGGRESSIVE_LOAD =
        new Field(ObjectType.class, "aggressiveLoad", AggressiveLoad.class);
    public static final Field JOIN_PATHS =
        new Field(ObjectType.class, "joinPaths", JoinPath.class);


    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onObjectType(this);
    }

    public String getQualifiedName() {
        return getFile().getModel().getName() + "." + getName().getName();
    }

    public Identifier getName() {
        return (Identifier) get(NAME);
    }

    public Type getExtends() {
        return (Type) get(EXTENDS);
    }

}
