package com.arsdigita.persistence.pdl.ast;

import java.util.*;

/**
 * Traversal
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 **/

abstract class Traversal {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/pdl/ast/Traversal.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    public void act(Node node) {}

    public void traverse(Node node) {
        act(node);
        for (Iterator it = node.getChildren().iterator(); it.hasNext(); ) {
            traverse((Node) it.next());
        }
    }

}
