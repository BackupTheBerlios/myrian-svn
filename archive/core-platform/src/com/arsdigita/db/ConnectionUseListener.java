/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.db;

/**
 * <b><font color="red">Experimental</font></b>
 * 
 * A simple listener interface to allow other classes to be
 * notified when a connection's users drop to zero. 
 *
 * @author <a href="mailto:eison@arsdigita.com">David Eison</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 * @since 4.6
 */
public interface ConnectionUseListener {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/ConnectionUseListener.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * Called when a connection has zero users.
     */
    public void connectionUserCountHitZero(com.arsdigita.db.Connection conn) 
            throws java.sql.SQLException;
}
