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

/**
 * NullException
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #1 $ $Date: 2003/12/10 $
 **/

public class NullException extends ProtoException {

    public final static String versionId = "$Id: //core-platform/test-qgen/src/com/redhat/persistence/NullException.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    private Role m_role;

    NullException(Role role) {
	m_role = role;
    }

    public Role getRole() {
	return m_role;
    }

}
