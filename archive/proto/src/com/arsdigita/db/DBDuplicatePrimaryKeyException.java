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

package com.arsdigita.db;

import java.sql.SQLException;

/**
 * This defines the exception that persistent objects should throw
 * when someone tries to add an object to the database with a
 * primary key that is already used.
 *
 * @author Kevin Scaldeferri
 */

public class DBDuplicatePrimaryKeyException extends SQLException {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/db/DBDuplicatePrimaryKeyException.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    public DBDuplicatePrimaryKeyException() {
        super();
    }

    public DBDuplicatePrimaryKeyException(String msg) {
        super(msg);
    }
}
