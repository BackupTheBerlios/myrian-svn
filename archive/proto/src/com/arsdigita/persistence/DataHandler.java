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
 * The DataHandler can be used to override the behavior of the persistence
 * layer when it performs certain operations. Currently only delete is
 * supported since the primary use for this class is to override hard deletes
 * and turn them into soft deletes under certain circumstances.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2003/08/04 $
 **/

public abstract class DataHandler {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/DataHandler.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";


    /**
     * This method is called in order to delete a data object. By default this
     * executes the SQL defined in the object type definition for the given
     * data object. In the common case this does a hard delete.
     **/

    public void doDelete(DataObject data) {
        throw new Error("not implemented");
    }

}
