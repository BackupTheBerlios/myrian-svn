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
package com.redhat.persistence.metadata;



/**
 * Element
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 **/

abstract class Element {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/metadata/Element.java#4 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private Object m_parent;
    private Object m_key;

    Object getParent() {
        return m_parent;
    }

    void setParent(Object parent) {
        m_parent = parent;
    }

    abstract Object getElementKey();

}
