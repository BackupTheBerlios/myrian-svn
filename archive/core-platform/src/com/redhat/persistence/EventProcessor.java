/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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
 */

package com.redhat.persistence;

/**
 * Event processor.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @since 2003-02-20
 * @version $Revision: #1 $ $Date: 2003/07/08 $
 **/
public abstract class EventProcessor {

    public final static String versionId =
        "$Id: //core-platform/dev/src/com/redhat/persistence/EventProcessor.java#1 $" +
        " by $Author: rhs $, $DateTime: 2003/07/08 21:04:28 $";

    /**
     * During this method, calls into the session to which the event processor
     * is attached are not allowed.
     *
     * @param isCommit indicates whether the clean up corresponds to a
     * commit. If true, the event processor should throw an exception if it is
     * not in a consistent state. This exception will prevent the commit
     * from actually taking place. If false, exceptions should not be thrown.
     */
    protected abstract void cleanUp(boolean isCommit);

    protected abstract void write(Event ev);

    protected abstract void flush();
}
