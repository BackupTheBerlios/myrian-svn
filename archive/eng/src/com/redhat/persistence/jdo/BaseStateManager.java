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
