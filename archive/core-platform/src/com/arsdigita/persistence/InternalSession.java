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

/**
 * Package private view of the Session implementation. Any implementation must implement this interface,
 * not Session. Used to expose certain methods only to the Persistence subsystem.
 */
interface InternalSession extends Session {

    /**
     * Adds a data object to this session for the purposes of tracking which
     * data objects participate in a given transaction.
     **/
    void addDataObject(DataObject obj);

    /**
     * Detaches any data objects from this session. To be called when
     * a transaction ends.
     *
     * @param valid True if the data objects are clean, false if they may be
     *              dirtied by transaction rollbacks, etc.
     **/

    void disconnectDataObjects(boolean valid);

    /**
     * Returns the datastore in use by this session.
     */
    DataStore getDataStore();

}
