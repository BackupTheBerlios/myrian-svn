/*
 * Copyright (C) 2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.pdl.adapters;

import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;

/**
 * IdentityAdapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class IdentityAdapter extends Adapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/IdentityAdapter.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public Object getObject(ObjectType basetype,
                            PropertyMap props,
                            Session ssn) {
        return props;
    }

    public PropertyMap getProperties(Object obj) {
        return (PropertyMap) obj;
    }

    public ObjectType getObjectType(Object obj) {
        return ((PropertyMap) obj).getObjectType();
    }

}
