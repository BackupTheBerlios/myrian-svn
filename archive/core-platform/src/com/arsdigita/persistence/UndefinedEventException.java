/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
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
 * @version $Revision: #7 $ $Date: 2004/03/30 $
 */

public class UndefinedEventException extends PersistenceException {


    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/UndefinedEventException.java#7 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    public UndefinedEventException(String msg) {
        super(msg, null);
    }

}
