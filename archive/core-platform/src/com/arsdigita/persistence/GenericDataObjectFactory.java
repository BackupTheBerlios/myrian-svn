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

import com.arsdigita.persistence.metadata.ObjectType;

import java.lang.reflect.Constructor;

/**
 * Title:       GenericDataObjectFactory class
 * Description: The class is used to instantiate DataObjects using the meta
 *              data passed in.
 * Copyright:   Copyright (c) 2001
 * Company:     ArsDigita
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/22 $
 */

public class GenericDataObjectFactory {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/GenericDataObjectFactory.java#4 $ by $Author: jorris $, $DateTime: 2002/08/22 10:38:41 $";


    /**
     * Creates a new data object using the appropriate java class
     * name as defined by the passed in object type. The returned
     * Object can then be cast to the more specific class associated
     * with the object type.
     *
     * @see com.arsdigita.persistence.metadata.ObjectType#getClassName()
     */
    public static GenericDataObject createObject(ObjectType type,
                                                 Session session,
                                                 boolean isNew) {
        try {
            GenericDataObject result;

            // XXX: String name = type.getClassName();
            String name = null;
            if (name == null) {
                result = new GenericDataObject();
            } else {
                Class cls = Class.forName(name);
                Constructor cons = cls.getConstructor(new Class[0]);
                result = (GenericDataObject) cons.newInstance(new Object[0]);
            }

            result.setObjectType(type);
            result.setNew(isNew);
            result.setSession(session);
            SessionManager.getInternalSession().addDataObject(result);

            return result;
        } catch (Exception e) {
            throw new Error("Exception of type: " + e.getClass().getName() +
                            "while creating a data object of type " +
                            type.getName() + ": " + e.getMessage());
        }
    }

}
