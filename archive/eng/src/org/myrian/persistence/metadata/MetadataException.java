/*
 * Copyright (C) 2003-2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.myrian.persistence.metadata;

import org.myrian.persistence.ProtoException;

/**
 * MetadataException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #2 $ $Date: 2004/10/04 $
 **/

public class MetadataException extends ProtoException {


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
