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

package com.arsdigita.util;

/**
 * A generic implementation of the Lockable interface.
 *
 * @see Lockable
 * 
 * @author <a href="mbryzek@arsdigita.com">Michael Bryzek</a>
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/LockableImpl.java#2 $ 
 **/

public class LockableImpl implements Lockable {

    private boolean m_locked = false;

    /**
     * Lock an object. Locked objetcs are to be considered immutable. Any
     * attempt to modify them, e.g., through a <code>setXXX</code> method 
     * should lead to an exception.
     * 
     * @see Lockable#lock()
     **/
    // must not be final so cms.ui.Grid.GridModelBuilder can override it.
    public void lock() {
        m_locked = true;
    }

    /**
     * Return whether an object is locked and thus immutable, or can still be
     * modified.
     * 
     * @see Lockable#isLocked()
     **/
    // must not be final so cms.ui.PropertySheet.PSTMBAdapter can override it.
    public boolean isLocked() {
        return m_locked;
    }
}
