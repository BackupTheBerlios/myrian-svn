/*
 * Copyright (C) 2003-2004 Red Hat, Inc.  All Rights Reserved.
 *
 * This program is Open Source software; you can redistribute it and/or
 * modify it under the terms of the Open Software License version 2.1 as
 * published by the Open Source Initiative.
 *
 * You should have received a copy of the Open Software License along
 * with this program; if not, you may obtain a copy of the Open Software
 * License version 2.1 from http://www.opensource.org/licenses/osl-2.1.php
 * or by writing the Open Source Initiative c/o Lawrence Rosen, Esq.,
 * 3001 King Ranch Road, Ukiah, CA 95482.
 */
package com.redhat.persistence;

/**
 * Event processor.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-28
 * @version $Revision: #2 $ $Date: 2004/08/30 $
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
