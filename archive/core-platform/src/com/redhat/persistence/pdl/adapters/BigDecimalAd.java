/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.pdl.adapters;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * BigDecimalAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public class BigDecimalAd extends SimpleAdapter {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/pdl/adapters/BigDecimalAd.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public BigDecimalAd() {
	super("global.BigDecimal", Types.NUMERIC);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        ps.setBigDecimal(index, (BigDecimal) obj);
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
        return rs.getBigDecimal(column);
    }

}
