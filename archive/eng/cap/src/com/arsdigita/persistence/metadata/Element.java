/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.persistence.metadata;

import com.redhat.persistence.metadata.Root;

import org.apache.log4j.Logger;

/**
 * The Element class is the abstract base class for functionality common to
 * all metadata classes.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 */

abstract public class Element {

    public final static String versionId = "$Id: //eng/persistence/dev/cap/src/com/arsdigita/persistence/metadata/Element.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
