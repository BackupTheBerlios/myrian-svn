package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * ReferenceKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/05/12 $
 **/

public class ReferenceKeyNd extends StatementNd {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/proto/pdl/nodes/ReferenceKeyNd.java#1 $ by $Author: ashah $, $DateTime: 2003/05/12 18:19:45 $";

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
