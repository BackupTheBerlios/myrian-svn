package com.redhat.persistence.oql;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

/**
 * IdentityAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2004/02/04 $
 **/

public class IdentityAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/test-qgen/test/src/com/redhat/persistence/oql/IdentityAdapter.java#1 $ by $Author: rhs $, $DateTime: 2004/02/04 11:20:32 $";

    public Object getObject(ObjectType basetype, PropertyMap props) {
        return props;
    }

    public PropertyMap getProperties(Object obj) {
        return (PropertyMap) obj;
    }

    public ObjectType getObjectType(Object obj) {
        return ((PropertyMap) obj).getObjectType();
    }

}
