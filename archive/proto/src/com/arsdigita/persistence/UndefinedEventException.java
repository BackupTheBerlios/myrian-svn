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
 * UndefinedEventException - Thrown when the persistence system attempts to invoke
 * an event, such as a property retrieve event, which is undefined.
 *
 * @author Jon Orris
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 */

public class UndefinedEventException extends PersistenceException {


    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/UndefinedEventException.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 09:48:41 $";

    public UndefinedEventException(String msg) {
        super(msg, null);
    }

}
