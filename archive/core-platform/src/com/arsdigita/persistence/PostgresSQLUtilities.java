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

package com.arsdigita.persistence;


import com.arsdigita.persistence.sql.SQL;
import com.arsdigita.persistence.sql.Element;
import com.arsdigita.persistence.sql.Identifier;

import com.arsdigita.persistence.metadata.Property;

import java.util.List;
import java.io.StringReader;

/**
 * This manipulates SQL in a way that is specific to Postgres. 
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/07/17 $
 */

class PostgresSQLUtilities implements SQLUtilities  {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/PostgresSQLUtilities.java#1 $ by $Author: randyg $, $DateTime: 2002/07/17 16:18:39 $";

    /**
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
     *  In postgres, this will return something like "is not null"
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
