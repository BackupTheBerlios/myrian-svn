package com.redhat.persistence.oql;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/23 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Expression.java#2 $ by $Author: dennis $, $DateTime: 2004/03/23 03:39:40 $";

    public static Expression valueOf(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(valueOf(path.getParent()), path.getName());
        }
    }

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract String summary();

}
