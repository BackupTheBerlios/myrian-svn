package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.*;
import java.util.*;

/**
 * Expression
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/03/11 $
 **/

public abstract class Expression {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/oql/Expression.java#1 $ by $Author: vadim $, $DateTime: 2004/03/11 18:13:02 $";

    abstract void frame(Generator generator);

    abstract Code emit(Generator generator);

    abstract String summary();

}
