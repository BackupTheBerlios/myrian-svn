package com.arsdigita.persistence.proto;

import com.arsdigita.persistence.proto.common.*;
import com.arsdigita.persistence.proto.metadata.*;

/**
 * QuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/02/26 $
 **/

public abstract class QuerySource {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/proto/QuerySource.java#1 $ by $Author: rhs $, $DateTime: 2003/02/26 12:01:31 $";

    public abstract Query getQuery(ObjectType type, Object key);

    // These should probably be changed to take type signatures.
    public abstract Query getQuery(Object obj);

    public abstract Query getQuery(Object obj, Property prop);

}
