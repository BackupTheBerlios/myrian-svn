/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.redhat.persistence.oql;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

/**
 * IdentityAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/03/30 $
 **/

public class IdentityAdapter extends Adapter {

    public final static String versionId = "$Id: //core-platform/dev/test/src/com/redhat/persistence/oql/IdentityAdapter.java#2 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
