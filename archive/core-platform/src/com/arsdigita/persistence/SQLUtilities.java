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

package com.arsdigita.persistence;


import com.arsdigita.persistence.sql.Element;

/**
 *  This is the interface for random SQL utilities that are needed
 *  to allow the system to work with different databases.  For instance
 *  the Oracle instance of this replaces "= null" with "is null"
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #5 $ $Date: 2002/11/01 $
 */

public interface SQLUtilities  {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SQLUtilities.java#5 $ by $Author: vadim $, $DateTime: 2002/11/01 09:30:48 $";

    /**
     *  This takes the SQL Element and the source which contains variable
     *  values and processes them accordingly.  It then returns
     *  the resulting SQL element.  For instance, for the
     *  OracleSQLUtilities.processNulls, this will replace all
     *  bind variables with the value of null with "is null" or
     *  "is not null"
     *  @deprecated This is a no-op so it will be removed.
     */
    Element processNulls(Element sql, DataContainer source);


    /**
     *  This method takes a comparator (e.g. =, <=, >=, !=, <>)
     *  as well as a variable name and then creates the appropriate
     *  string given that the value of the variable is null.
     *  In oracle, this will return something like "is not null"
     *  or "is null"  Other databases will have something like
     *  <code>comparator + " :" + variableName<code>
     */
    String createNullString(String comparator, String variableName);
}
