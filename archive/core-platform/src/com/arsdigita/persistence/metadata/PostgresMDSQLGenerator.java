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

package com.arsdigita.persistence.metadata;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Category;

/**
 * A interface that defines an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the 
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see ObjectEvent ).
 *
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#2 $
 * @since 4.6.3
 */

class PostgresMDSQLGenerator extends BaseMDSQLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/PostgresMDSQLGenerator.java#2 $ by $Author: randyg $, $DateTime: 2002/07/18 10:31:38 $";

    private static Category s_log = 
        Category.getInstance(PostgresMDSQLGenerator.class);

    /**
     * Generates an Event of a particular Event type for a certain
     * ObjectType.  New Event is automatically added to the object type
     * metadata.
     *
     * @param type the object type to create an event for
     * @param eventType the Event type.  These are the types specified
     * in {@link com.arsdigita.persistence.metadata.CompoundType}
     * @return the new Event, or null if it could not be created
     */
    public Event generateEvent(ObjectType type, int eventType) {
        return super.generateEvent(type, eventType);
    }


    /**
     * Generates an Event of a particular type for a certain Property.
     * New event is automatically added to the Property.
     *
     * @param type the ObjectType the propery belongs to
     * @param prop the Property to generate an event for
     * @param eventType the Event type.  These are the types specified in
     *                  {@link com.arsdigita.persistence.metadata.Property}
     * @return the new Event, or null if it could not be created
     */
    public Event generateEvent(ObjectType type, Property prop, int eventType) {
        return super.generateEvent(type, prop, eventType);
    }
}
