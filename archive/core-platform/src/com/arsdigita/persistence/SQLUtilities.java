/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence;


/**
 *  This is the interface for random SQL utilities that are needed
 *  to allow the system to work with different databases.  For instance
 *  the Oracle instance of this replaces "= null" with "is null"
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/03/30 $
 */

public interface SQLUtilities  {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/SQLUtilities.java#8 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

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
