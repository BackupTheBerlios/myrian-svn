/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.ObjectType;

/**
 * Source
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/02/24 $
 **/

public class Source {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/Source.java#2 $ by $Author: ashah $, $DateTime: 2004/02/24 12:49:36 $";

    private ObjectType m_type;
    private Path m_path;

    public Source(ObjectType type) {
        this(type, null);
    }

    public Source(ObjectType type, Path path) {
        m_type = type;
        m_path = path;
    }

    public ObjectType getObjectType() {
        return m_type;
    }

    public Path getPath() {
        return m_path;
    }

    public String toString() {
        return "<source " + m_path + "," + m_type.getQualifiedName() + ">";
    }
}
