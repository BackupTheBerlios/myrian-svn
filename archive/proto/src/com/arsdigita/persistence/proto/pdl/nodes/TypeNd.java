package com.arsdigita.persistence.proto.pdl.nodes;

import java.util.*;

/**
 * Type
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/01/15 $
 **/

public class TypeNd extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/TypeNd.java#1 $ by $Author: rhs $, $DateTime: 2003/01/15 10:39:47 $";

    public static final Field IDENTIFIERS =
        new Field(TypeNd.class, "identifiers", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onType(this);
    }

    private Collection getIdentifiers() {
        return (Collection) get(IDENTIFIERS);
    }

    public boolean isQualified() {
        return getIdentifiers().size() > 1;
    }

    public String getName() {
        return ((IdentifierNd) getIdentifiers().iterator().next()).getName();
    }

    public String getQualifiedName() {
        if (!isQualified()) {
            throw new IllegalArgumentException
                ("Not a qualified type");
        }
        final StringBuffer result = new StringBuffer();
        traverse(new Switch() {
                public void onIdentifier(IdentifierNd id) {
                    if (result.length() > 0) {
                        result.append('.');
                    }
                    result.append(id.getName());
                }
            });
        return result.toString();
    }

}
