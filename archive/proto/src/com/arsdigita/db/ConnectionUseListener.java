/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.db;

/**
 * 
 *
 * A simple listener interface to allow other classes to be
 * notified when a connection's users drop to zero.
 *
 * @author David Eison
 * @version $Revision: #2 $ $Date: 2003/04/09 $
 * @since 4.6
 */
public interface ConnectionUseListener {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/db/ConnectionUseListener.java#2 $ by $Author: rhs $, $DateTime: 2003/04/09 16:35:55 $";

    /**
     * Called when a connection has zero users.
     */
    public void connectionUserCountHitZero(com.arsdigita.db.Connection conn)
        throws java.sql.SQLException;
}
