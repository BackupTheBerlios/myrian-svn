/*
 * Copyright (C) 2001-2004 Red Hat, Inc.  All Rights Reserved.
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
package com.arsdigita.util;

/**
 * A common interface for all lockable parts of ACS. The locking
 * mechanism makes it possible to safely split all data structures
 * that are used inside a web server into ones that are constant
 * across all requests and those that may change during a request.
 *
 * <p> The distinction betwen static and request-specific data helps in
 * optimizing the amount of memory that needs to be allocated for each
 * request. Data structures that are static can be allocated and
 * initialized ahead of time, e.g., in the <code>init</code> method of a
 * servlet. The initialized data structures are then <em>locked</em> to
 * make them immutable. This mechanism ensures that static structures that
 * are supposed to be shared by many requests, often even concurrently, do
 * not change and are not "polluted" by request-specific data.
 *
 * <p> There is no automatic mechanism that makes an object immutable by
 * itself. The right checks and operations need to be implemented by each
 * class implementing <code>Lockable</code>.
 *
 * <p> Bebop parameters are a good example of how one logical structure is
 * split into two classes: the class {@link
 * com.arsdigita.bebop.parameters.ParameterModel} is <code>Lockable</code>
 * and only contains the description of the parameter in an HTTP request
 * that is static and does not change on a per-request basis, such as the
 * name of the parameter and the (Java) type that the parameter value
 * should be converted to. The class {@link
 * com.arsdigita.bebop.parameters.ParameterData} contains all the
 * request-specific information for a parameter, such as the value of the
 * parameter.
 *
 * <p> Any class that implements <code>Lockable</code> is expected to be
 * fully modifiable until its {@link #lock} method is called. From that
 * point on, it is read-only and should throw exceptions whenever an
 * attempt is made to modify it.
 *
 * @author David Lutterkort 
 * @version $Id: //eng/persistence/dev/src/com/arsdigita/util/Lockable.java#2 $ */

public interface Lockable {

    public static final String versionId = "$Id: //eng/persistence/dev/src/com/arsdigita/util/Lockable.java#2 $ by $Author: dennis $, $DateTime: 2004/08/30 14:24:55 $";
    /**
     * Lock an object. Locked objects are to be considered immutable. Any
     * attempt to modify them, e.g., through a <code>setXXX</code> method
     * should lead to an exception.
     *
     * <p> Most lockable Bebop classes throw an {@link
     * java.lang.IllegalStateException} if an attempt is made to modify a
     * locked instance.
     */
    void lock();

    /**
     * Return whether an object is locked and thus immutable, or can still be
     * modified.
     */
    boolean isLocked();
}
