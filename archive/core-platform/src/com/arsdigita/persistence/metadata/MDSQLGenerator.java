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

package com.arsdigita.persistence.metadata;


/**
 * A interface that defines an API to automatically generate SQL queries based
 * on the metadata provided in the PDL files.  The primary interface is the
 * generateSQL function, which will generate an event for an object type/event
 * type combination ( @see ObjectEvent ).
 *
 * @author Patrick McNeill
 * @version $Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/MDSQLGenerator.java#8 $
 * @since 4.6.3
 */

public interface MDSQLGenerator {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/persistence/metadata/MDSQLGenerator.java#8 $ by $Author: dennis $, $DateTime: 2002/12/11 13:49:53 $";
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
    Event generateEvent(ObjectType type, int eventType);

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
    public Event generateEvent(ObjectType type, Property prop, int eventType, ObjectType link);

    /**
     *  This generates events specifically for associations.  That is,
     *  if an association requires a different type of event (e.g. an
     *  update) then this will call the associations event.  Otherwise,
     *  it delegates to {@link #generateEvent}
     */
    public Event generateAssociationEvent(ObjectType type, int eventType);
}
