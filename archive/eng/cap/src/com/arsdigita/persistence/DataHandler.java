/*
 * Copyright (C) 2002-2004 Red Hat, Inc.  All Rights Reserved.
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
 * The DataHandler can be used to override the behavior of the persistence
 * layer when it performs certain operations. Currently only delete is
 * supported since the primary use for this class is to override hard deletes
 * and turn them into soft deletes under certain circumstances.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public abstract class DataHandler {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/DataHandler.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";


    /**
     * This method is called in order to delete a data object. By default this
     * executes the SQL defined in the object type definition for the given
     * data object. In the common case this does a hard delete.
     **/

    public void doDelete(DataObject data) {
        throw new Error("not implemented");
    }

}
