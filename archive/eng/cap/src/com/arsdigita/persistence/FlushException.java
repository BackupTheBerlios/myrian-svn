/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.arsdigita.persistence;

/**
 * An exception representing the inability to write all changes to the
 * database. It is caused by some required properties being set to null.
 */
public class FlushException extends PersistenceException {

    public static final String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/FlushException.java#4 $ by $Author: vadim $, $DateTime: 2004/10/01 18:41:18 $";

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException (org.myrian.persistence.FlushException e) {
        super(e);
    }

    /**
     * No public constructor.
     * Should only be constructed by PersistenceException.newInstance.
     */
    protected FlushException(String msg,
                             org.myrian.persistence.FlushException e) {
        super(msg, e);
    }

}
