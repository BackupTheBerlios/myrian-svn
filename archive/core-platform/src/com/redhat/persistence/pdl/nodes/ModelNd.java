package com.redhat.persistence.pdl.nodes;

/**
 * Model
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public class ModelNd extends Node {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/nodes/ModelNd.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public static final Field PATH =
        new Field(ModelNd.class, "path", IdentifierNd.class, 1);

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onModel(this);
    }

    public String getName() {
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
