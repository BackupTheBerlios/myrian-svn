/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.persistence;


/**
 * UndefinedEventException - Thrown when the persistence system attempts to invoke
 * an event, such as a property retrieve event, which is undefined.
 *
 * @author Jon Orris
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */

public class UndefinedEventException extends PersistenceException {


    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/UndefinedEventException.java#9 $ by $Author: dennis $, $DateTime: 2004/08/16 18:10:38 $";

    public UndefinedEventException(String msg) {
        super(msg, null);
    }

}
