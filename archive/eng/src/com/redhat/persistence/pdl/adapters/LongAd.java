/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


/**
 * LongAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class LongAd extends SimpleAdapter {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/pdl/adapters/LongAd.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public LongAd() {
	super("global.Long", Types.BIGINT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
	ps.setLong(index, ((Long) obj).longValue());
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	long l = rs.getLong(column);
	if (rs.wasNull()) {
	    return null;
	} else {
	    return new Long(l);
	}
    }

}
