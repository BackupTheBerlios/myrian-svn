package com.arsdigita.persistence.pdl.ast;

import java.util.*;

/**
 * Traversal
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2002/07/18 $
 **/

abstract class Traversal {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Traversal.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public void act(Node node) {}

    public void traverse(Node node) {
        act(node);
        for (Iterator it = node.getChildren().iterator(); it.hasNext(); ) {
            traverse((Node) it.next());
        }
    }

}
