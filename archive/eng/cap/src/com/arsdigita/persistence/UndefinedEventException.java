/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence;


/**
 * UndefinedEventException - Thrown when the persistence system attempts to invoke
 * an event, such as a property retrieve event, which is undefined.
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

public class UndefinedEventException extends PersistenceException {


    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/UndefinedEventException.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

    public UndefinedEventException(String msg) {
        super(msg, null);
    }

}
