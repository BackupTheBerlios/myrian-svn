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
 * Simple listener for result set events.
 *
 * @author David Eison
 * @version $Id: //core-platform/proto/src/com/arsdigita/db/ResultSetEventListener.java#4 $
 * @since 4.6
 */
public interface ResultSetEventListener extends java.util.EventListener {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/db/ResultSetEventListener.java#4 $ by $Author: dennis $, $DateTime: 2003/08/04 16:15:53 $";

    /**
     * Called when resultSet closes.
     */
    public void resultSetClosed(ResultSetEvent event) throws java.sql.SQLException;

    // TODO: Consider error notification event?
}
