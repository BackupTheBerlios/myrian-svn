/*
 * Copyright (C) 2004 Red Hat, Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.redhat.persistence.jdo;

import javax.jdo.PersistenceManager;
import javax.jdo.spi.PersistenceCapable;
import javax.jdo.spi.StateManager;

/**
 * This is a utility for partial implementations of StateManager. It extends
 * AbstractStateManager and implements abstract methods with
 * IllegalStateExceptions
 */
class BaseStateManager extends AbstractStateManager {

    public Object getObjectId(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public PersistenceManager getPersistenceManager(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public Object getObjectField(PersistenceCapable pc, int field,
                                 Object currentValue) {
        throw new IllegalStateException("not implemented");
    }

    public Object getTransactionalObjectId(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isDeleted(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isDirty(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isLoaded(PersistenceCapable pc, int field) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isNew(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isPersistent(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public boolean isTransactional(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public void makeDirty(PersistenceCapable pc, String fieldName) {
        throw new IllegalStateException("not implemented");
    }

    public void preSerialize(PersistenceCapable pc) {
        throw new IllegalStateException("not implemented");
    }

    public StateManager replacingStateManager(PersistenceCapable pc,
                                              StateManager sm) {
        return sm;
    }

    public void setObjectField(PersistenceCapable pc, int field,
                               Object currentValue, Object newValue) {
        throw new IllegalStateException("not implemented");
    }
}
