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
package com.redhat.persistence.jdo;

import java.util.*;

/**
 * Rolodex
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Rolodex {

    public final static String versionId = "$Id: //eng/persistence/dev/test/src/com/redhat/persistence/jdo/Rolodex.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private Set m_contacts = new HashSet();

    public Rolodex() {}

    public Collection getContacts() {
        return m_contacts;
    }

}
