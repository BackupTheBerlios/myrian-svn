/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence;

import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.ObjectType;

/**
 * Source
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/03/30 $
 **/

public class Source {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/Source.java#5 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
