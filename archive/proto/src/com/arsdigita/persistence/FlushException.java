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

/**
 * An exception representing the inability to write all changes to the
 * database. It is caused by some required properties being set to null.
 */
public class FlushException extends PersistenceException {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/FlushException.java#2 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException (com.redhat.persistence.FlushException e) {
        super(e);
    }

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException(String msg,
                             com.redhat.persistence.FlushException e) {
        super(msg, e);
    }

}
