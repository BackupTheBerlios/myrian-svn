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

package com.arsdigita.infrastructure;

//import com.arsdigita.util.RuntimeException;

/**
 * A multi-row data source.  The rows are acessible sequentially.
 * Each row should have the same parameters, and map parameter names
 * to values.  A RowSequence typically results from a database query.
 * Caution: adding methods to this interface would break persistence,
 * as {@link com.arsdigita.persistence.DataQuery} extends it.
 *
 * @author <a href="mailto:christian@arsdigita.com">Christian
 * Brechb&uuml;hler</a>
 * @version $Revision: #1 $ $Date: 2002/05/12 $
 * */

public interface RowSequence {

    String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/RowSequence.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";

    /**
     * Rewinds the row sequence to the beginning.  It's as if next() was
     * never called.
     **/
    void rewind();


    /**
     * Returns the value of the <i>propertyName</i> property associated with
     * the current position in the sequence.
     *
     * @param propertyName the name of the property
     *
     * @return the value of the property
     **/
    Object get(String propertyName);


    /**
     * Returns the current position within the sequence. The first
     * position is 1.
     *
     * @return the current position; 0 if there is no current position
     **/
    int getPosition();

    /**
     * Moves the cursor to the next row in the sequence.
     *
     * @return true if the new current row is valid; false if there are no
     *         more rows.
     **/
    boolean next();

    /**
     * Returns the size of this query.
     * @return the number of rows.
     **/
    long size();
}
