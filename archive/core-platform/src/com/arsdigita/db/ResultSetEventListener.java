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
 * Simple listener for result set events.
 *
 * @author <a href="mailto:eison@arsdigita.com">David Eison</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEventListener.java#2 $
 * @since 4.6
 */
public interface ResultSetEventListener extends java.util.EventListener {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEventListener.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    /**
     * Called when resultSet closes.
     */
    public void resultSetClosed(ResultSetEvent event) throws java.sql.SQLException;

    // TODO: Consider error notification event?
}
