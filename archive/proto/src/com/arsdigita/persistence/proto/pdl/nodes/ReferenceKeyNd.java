package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ReferenceKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2003/01/30 $
 **/

public class ReferenceKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/ReferenceKeyNd.java#2 $ by $Author: rhs $, $DateTime: 2003/01/30 17:57:25 $";

    public static final Field COLUMN =
        new Field(ReferenceKeyNd.class, "column", ColumnNd.class, 1, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onReferenceKey(this);
    }

    public ColumnNd getCol() {
        return (ColumnNd) get(COLUMN);
    }

}
