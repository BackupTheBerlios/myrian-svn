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

package com.arsdigita.persistence.pdl.ast;

import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;

/**
 * Defines a generic MapStatement (ie a Binding or a Mapping)
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/11/27 $
 */

public abstract class MapStatement extends Element {

    public final static String versionId = "$Id: //core-platform/proto/src/com/arsdigita/persistence/pdl/ast/MapStatement.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    abstract public String[] getPath();

    public String getPrettyPath() {
        StringBuffer result = new StringBuffer();
        String[] path = getPath();
        for (int i = 0; i < path.length; i++) {
            result.append(path[i]);
            if (i < path.length - 1) {
                result.append('.');
            }
        }

        return result.toString();
    }

    void validateMapping(ObjectType start) {
        String[] path = getPath();

        ObjectType type = start;
        for (int i = 0; i < path.length; i++) {
            Property prop = type.getProperty(path[i]);
            if (prop == null) {
                error(
                      "Invalid mapping, object type " +
                      start.getQualifiedName() +
                      " has no such property: " + getPrettyPath()
                      );
            }

            if (i < path.length - 1) {
                try {
                    type = (ObjectType) prop.getType();
                } catch (ClassCastException e) {
                    error(
                          "Invalid mapping, object type " +
                          start.getQualifiedName() +
                          " has no such property: " + getPrettyPath()
                          );
                }
            }
        }
    }
}
