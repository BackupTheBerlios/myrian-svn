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
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @since 2003-02-20
 * @version $Revision: #2 $ $Date: 2004/08/30 $
 **/
public abstract class EventProcessor {

    public final static String versionId =
        "$Id: //eng/persistence/dev/src/com/redhat/persistence/EventProcessor.java#2 $" +
        " by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";

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
