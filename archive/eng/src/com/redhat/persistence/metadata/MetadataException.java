/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence.metadata;

import com.redhat.persistence.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/

public class MetadataException extends ProtoException {

    public final static String versionId = "$Id: //eng/persistence/dev/src/com/redhat/persistence/metadata/MetadataException.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
