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
 *  Link -
 * This class is used to represent the relationship between
 * two object types.  This is the same terminology that is used within
 * UML.  Links may contain properties (e.g. the property isDefault could
 * be used to indicate whether this is the defaut link for the object).
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public class Link {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/Link.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    private DataContainer m_data;

    /**
     * Returns the value associated with the given property.
     *
     * @param propertyName The property name.
     *
     * @return The property value.
     **/

    public Object get(String propertyName) {
        return m_data.get(propertyName);
    }

    /**
     * Sets the specified property to the specified value.
     *
     * @param propertyName The property name.
     * @param value The property value.
     **/

    public void set(String propertyName, Object value) {
        m_data.set(propertyName, value);
    }

}