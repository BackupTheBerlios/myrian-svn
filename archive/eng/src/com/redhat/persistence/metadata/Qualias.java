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
package com.redhat.persistence.metadata;

import com.redhat.persistence.common.*;

import java.util.*;

/**
 * Qualias
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/08/30 $
 **/

public class Qualias extends Mapping {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/Qualias.java#3 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    private String m_query;

    public Qualias(Path path, String query) {
        super(path);
        m_query = query;
    }

    public List getColumns() {
        throw new UnsupportedOperationException();
    }

    public String getQuery() {
        return m_query;
    }

    public Table getTable() {
        return null;
    }

    public void dispatch(Switch sw) {
        sw.onQualias(this);
    }

}
