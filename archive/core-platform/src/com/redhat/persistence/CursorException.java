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

/**
 * CursorException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #3 $ $Date: 2004/03/30 $
 **/

public class CursorException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/dev/src/com/redhat/persistence/CursorException.java#3 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private Cursor m_cursor;

    CursorException(Cursor c, String msg) {
	super(msg);
	m_cursor = c;
    }

    public Cursor getCursor() {
	return m_cursor;
    }

}
