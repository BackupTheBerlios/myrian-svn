/*
 * Copyright (C) 2003, 2003 Red Hat Inc. All Rights Reserved.
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
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-28
 * @version $Revision: #2 $ $Date: 2003/08/19 $
 **/
public interface EventProcessorManager {
    /**
     * Returns the singleton instance of {@link EventProcessor} for this thread.
     * Each thread must have its own. In other words,
     * <code>EventProcessorManager</code> and <code>EventProcessor</code> must
     * have the same semantics as {@link
     * com.arsdigita.persistence.SessionManager} and {@link
     * com.arsdigita.persistence.Session}.
     **/
    EventProcessor getEventProcessor();
}
