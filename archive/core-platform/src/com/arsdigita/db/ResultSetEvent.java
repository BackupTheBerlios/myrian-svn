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
 * Provides information about the source of a result set event.
 *
 * @author <a href="mailto:eison@arsdigita.com">David Eison</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEvent.java#1 $
 * @since 4.6
 */
public class ResultSetEvent extends java.util.EventObject {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/db/ResultSetEvent.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

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
