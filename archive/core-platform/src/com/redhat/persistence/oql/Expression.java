package com.redhat.persistence.oql;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/28 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Expression.java#3 $ by $Author: rhs $, $DateTime: 2004/03/28 22:52:45 $";

    public static Expression valueOf(Path path) {
        if (path.getParent() == null) {
            return new Variable(path.getName());
        } else {
            return new Get(valueOf(path.getParent()), path.getName());
        }
    }

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract void hash(Generator generator);

    abstract String summary();

}
