/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.db;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * the Sequence class provides functionality akin to Oracle sequences,
 * i.e. unique integer values appropriate for use as primary keys
 *n
 * this abstract class should be implemented by concrete classes
 * appropriate to a given database
 *
 * @author <A HREF="mailto:kevin@arsdigita.com">Kevin Scaldeferri</A>
 */

public abstract class SequenceImpl {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/SequenceImpl.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    public abstract BigDecimal getCurrentValue() throws SQLException;

    public abstract BigDecimal getNextValue() throws SQLException;

    public abstract BigDecimal getCurrentValue(Connection conn) 
        throws SQLException;

    public abstract BigDecimal getNextValue(Connection conn) 
        throws SQLException;
}
