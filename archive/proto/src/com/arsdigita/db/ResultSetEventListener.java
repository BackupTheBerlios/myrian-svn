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
 * @author <a href="mailto:eison@arsdigita.com">David Eison</a>
 * @version $Id: //core-platform/proto/src/com/arsdigita/db/ResultSetEventListener.java#1 $
 * @since 4.6
 */
public interface ResultSetEventListener extends java.util.EventListener {

    public static final String versionId = "$Id: //core-platform/proto/src/com/arsdigita/db/ResultSetEventListener.java#1 $ by $Author: dennis $, $DateTime: 2002/11/27 19:51:05 $";

    /**
     * Called when resultSet closes.
     */
    public void resultSetClosed(ResultSetEvent event) throws java.sql.SQLException;

    // TODO: Consider error notification event?
}
