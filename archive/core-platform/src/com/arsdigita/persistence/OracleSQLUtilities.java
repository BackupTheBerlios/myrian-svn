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
 * This manipulates SQL in a way that is specific to Oracle.  For
 * instance, it has the ability to replace bind variables with the
 * value of null with "is null" or "is not null"
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 */

class OracleSQLUtilities implements SQLUtilities  {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/OracleSQLUtilities.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     *  This takes the SQL Element and the source which contains variable
     *  values and processes them accordingly.  It then returns
     *  the resulting SQL element.  For instance, for the 
     *  OracleSQLUtilities.processNulls, this will replace all
     *  bind variables with the value of null with "is null" or 
     *  "is not null"
     *  This code does not work so it currently just returns the
     *  passed in element
     */
    public Element processNulls(Element sql, DataContainer source) {
        // this does not work so right now it does not do anything.
        return sql;
        /*
        StringBuffer buffer = new StringBuffer();
        List leafs = sql.getLeafElements();
            
        // we need both the "next element" and the "current element"
        // so that we can look ahead and if the next element
        // is a bind variable and the value is null then we do not
        // add the current or the next element.  Rather, we add the
        // "is null" or "is not null" and increment the counter appropriately
        Element nextElement = null;
        Element currentElement = null;
        String variableName = null;
        if (leafs.size() > 0) {
            currentElement = (Element)leafs.get(0);
        }
            
        for (int i = 1; i < leafs.size(); i++) {
            nextElement = (Element) leafs.get(i);
            if (nextElement.isBindVar()) {
                Identifier id = (Identifier) nextElement;
                variableName = id.toString().substring(1);

                // we look up the property so that we throw an
                // error if this variable was not bound to anything 
                String[] path = id.getPath();
                Property prop = source.lookupProperty(path);
                Object value = source.lookupValue(path);

                // the value is null so we swap the comparator and
                // bind variable with "is null" or "is not null"
                // depending on what is required
                if (value == null) {
                    if (currentElement.toString().indexOf("!") > -1 ||
                        currentElement.toString().indexOf("<>") > -1) {
                        buffer.append("is not null");
                    } else {
                        buffer.append("is null");
                    }

                    // we need to bump things up so that we do not repeat
                    // the same element twice
                    i += 1;
                    if (i + 1 < leafs.size()) {
                        currentElement = (Element)leafs.get(i);
                        continue;
                    } else {
                        // this means that the loop is not going to
                        // make another pass
                        if (i < leafs.size()) {
                            currentElement = (Element)leafs.get(i);
                        } else {
                            currentElement = null;
                        }
                        break;
                    }
                } else {
                    // the value is not null so just append the item
                    // as usual
                    buffer.append(currentElement.toString());
                }
            } else {
                if (currentElement != null) {
                    if (currentElement.isBindVar()) {
                        buffer.append(":" + variableName);
                    } else {
                        buffer.append(currentElement.toString());
                    }
                }
            }
            buffer.append(" ");
            currentElement = nextElement;
        }
        // Finally, we have to get the last element in the string
        if (currentElement != null) {
            if (currentElement.isBindVar()) {
                buffer.append(":" + variableName);
            } else {
                buffer.append(currentElement.toString());
            }
        } 

        sql = Element.parse(buffer.toString());
        return sql;
        */
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
