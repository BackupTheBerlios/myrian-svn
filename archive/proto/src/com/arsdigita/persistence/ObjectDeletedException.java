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
 * ObjectDeletedException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 **/

public class ObjectDeletedException extends PersistenceException {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/ObjectDeletedException.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    public ObjectDeletedException(String message) {
        super(message);
    }

}
