/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.persistence;

import java.sql.SQLException;

/**
 * An exception class that is used when a not available
 * exception is thrown by the database.
 *
 * This class is pretty much the same as one in com.arsdigita.db
 * because it's doing the same thing, but subclassed from
 * PersistenceException instead of SQLException.
 *
 * @author <A HREF="mailto:eison@arsdigita.com">David Eison</A>
 * @version $Revision: #1 $
 * @since 4.6
 */
public class DbNotAvailableException extends PersistenceException {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DbNotAvailableException.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected DbNotAvailableException(SQLException e) {
        super(e);
    }

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected DbNotAvailableException(String msg, SQLException e) {
        super(msg, e);
    }

}