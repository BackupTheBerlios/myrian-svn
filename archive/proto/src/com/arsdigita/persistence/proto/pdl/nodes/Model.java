package com.arsdigita.persistence.proto.pdl.nodes;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2002/12/31 $
 **/

public class Model extends Node {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/pdl/nodes/Model.java#1 $ by $Author: rhs $, $DateTime: 2002/12/31 15:39:17 $";

    public static final Field PATH =
        new Field(Model.class, "path", Identifier.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onModel(this);
    }

    public String getName() {
        final StringBuffer result = new StringBuffer();

        traverse(new Switch() {
                public void onIdentifier(Identifier id) {
                    if (result.length() > 0) {
                        result.append('.');
                    }
                    result.append(id.getName());
                }
            });

        return result.toString();
    }

}
