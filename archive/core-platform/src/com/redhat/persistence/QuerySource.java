package com.redhat.persistence;

import com.redhat.persistence.common.*;
import com.redhat.persistence.metadata.*;

/**
 * QuerySource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/

public abstract class QuerySource {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/QuerySource.java#1 $ by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    public abstract Query getQuery(ObjectType type);

    public abstract Query getQuery(PropertyMap keys);

    // These should probably be changed to take type signatures.
    public abstract Query getQuery(Object obj);

    public abstract Query getQuery(Object obj, Property prop);

}
