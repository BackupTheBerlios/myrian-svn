/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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
 * Provides information about the source of a result set event.
 *
 * @author David Eison
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEvent.java#5 $
 * @since 4.6
 */
public class ResultSetEvent extends java.util.EventObject {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEvent.java#5 $ by $Author: dennis $, $DateTime: 2003/08/15 13:46:34 $";

    /**
     * Creates a result set event with the specified source.
     */
    public ResultSetEvent(java.sql.ResultSet source) {
        super(source);
    }

    /**
     * Convenience method for getting the source.
     */
    public java.sql.ResultSet getResultSet() {
        return (java.sql.ResultSet)getSource();
    }
}
