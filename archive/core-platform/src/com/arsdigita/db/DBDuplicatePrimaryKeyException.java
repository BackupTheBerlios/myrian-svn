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
 * @author <A HREF="mailto:kevin@arsdigita.com">Kevin Scaldeferri</A>
 */

public class DBDuplicatePrimaryKeyException extends SQLException {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/DBDuplicatePrimaryKeyException.java#3 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    public DBDuplicatePrimaryKeyException() {
        super();
    }

    public DBDuplicatePrimaryKeyException(String msg) {
        super(msg);
    }
}
