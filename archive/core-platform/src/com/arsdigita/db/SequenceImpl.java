/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
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
 * @author Kevin Scaldeferri
 */

public abstract class SequenceImpl {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/SequenceImpl.java#8 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    public abstract BigDecimal getCurrentValue(Connection conn)
        throws SQLException;

    public abstract BigDecimal getNextValue(Connection conn)
        throws SQLException;
}
