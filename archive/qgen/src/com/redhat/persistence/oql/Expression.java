package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/03/09 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/oql/Expression.java#6 $ by $Author: rhs $, $DateTime: 2004/03/09 21:48:49 $";

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract String summary();

}
