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


import com.arsdigita.persistence.sql.SQL;
import com.arsdigita.persistence.sql.Element;
import com.arsdigita.persistence.sql.Identifier;

import com.arsdigita.persistence.metadata.Property;

import java.util.List;
import java.io.StringReader;

/**
 * This manipulates SQL in a way that is specific to Oracle.  For
 * instance, it has the ability to replace bind variables with the
 * value of null with "is null" or "is not null"
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #4 $ $Date: 2002/08/14 $
 */

class OracleSQLUtilities implements SQLUtilities  {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/OracleSQLUtilities.java#4 $ by $Author: dennis $, $DateTime: 2002/08/14 23:39:40 $";

    /**
     *  This takes the SQL Element and the source which contains variable
     *  values and processes them accordingly.  It then returns
     *  the resulting SQL element.  For instance, for the
     *  OracleSQLUtilities.processNulls, this will replace all
     *  bind variables with the value of null with "is null" or
     *  "is not null"
     *  This code does not work so it currently just returns the
     *  passed in element
     *  @deprecated This is a no-op so it will be removed.
     */
    public Element processNulls(Element sql, DataContainer source) {
        // this does not work so right now it does not do anything.
        return sql;
    }


    /**
     *  This method takes a comparator (e.g. =, <=, >=, !=, <>)
     *  as well as a variable name and then creates the appropriate
     *  string given that the value of the variable is null.
     *  In oracle, this will return something like "is not null"
     *  or "is null"  Other databases will have something like
     *  <code>comparator + " :" + variableName<code>
     */
    public String createNullString(String comparator, String variableName) {
        if (comparator.indexOf("!") > -1 || comparator.indexOf("<>") > -1) {
            return variableName + " is not null";
        } else {
            return variableName + " is null";
        }
    }

}
