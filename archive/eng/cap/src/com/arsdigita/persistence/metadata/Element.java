/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.metadata;

import com.redhat.persistence.metadata.Root;

import org.apache.log4j.Logger;

/**
 * The Element class is the abstract base class for functionality common to
 * all metadata classes.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #1 $ $Date: 2004/06/07 $
 */

abstract public class Element {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/Element.java#1 $ by $Author: rhs $, $DateTime: 2004/06/07 13:49:55 $";

    private static final Logger s_log =
        Logger.getLogger(Element.class.getName());


    private Root m_root;
    private Object m_obj;

    Element(Root root, Object obj) {
	m_root = root;
	m_obj = obj;
    }

    /**
     * Returns the filename for this metadata element.
     **/

    public String getFilename() {
        return m_root.getFilename(m_obj);
    }


    /**
     * Returns the line number for this metadata element.
     **/

    public int getLineNumber() {
        return m_root.getLine(m_obj);
    }


    /**
     * Returns the column number for this metadata element.
     **/

    public int getColumnNumber() {
        return m_root.getColumn(m_obj);
    }


    public int hashCode() {
	return m_obj.hashCode();
    }


    public boolean equals(Object other) {
	if (other instanceof Element) {
	    return m_obj.equals(((Element) other).m_obj);
	} else {
	    return false;
	}
    }

}
