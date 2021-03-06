/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.redhat.persistence.metadata;

import com.redhat.persistence.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/05/02 $
 **/

public class MetadataException extends ProtoException {

    public final static String versionId = "$Id: //users/rhs/persistence/src/com/redhat/persistence/metadata/MetadataException.java#3 $ by $Author: rhs $, $DateTime: 2004/05/02 13:12:27 $";

    private final Root m_root;
    private final Object m_element;

    public MetadataException(Root root, Object element, String msg) {
        super(message(root, element, msg), false);
        m_root = root;
        m_element = element;
    }

    private static String message(Root root, Object element, String msg) {
        if (root.hasLocation(element)) {
            return root.getFilename(element) + ": line " +
                root.getLine(element) + ", column " + root.getColumn(element) +
                ": " + msg;
        } else {
            return msg;
        }
    }

    public Root getRoot() {
        return m_root;
    }

    public Object getMetadataElement() {
        return m_element;
    }

}
