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
 * This is a utility for actual implementations of StateManager. It converts c
 * alls to methods with primitives to calls to the appropriate Object
 * method. It handles the callback interaction with PersistenceCapable
 * instances and provides the protected methods provideField, replaceField
 * replaceFlags for StateManager implementations.
 */
abstract class AbstractStateManager implements StateManager {

    // temporary storage for callback interaction with PC
    private Object m_tmpValue = null;
    private byte m_tmpByte = 0x0;

    public abstract Object getObjectId(PersistenceCapable pc);

    public abstract PersistenceManager getPersistenceManager(
        PersistenceCapable pc);

    public abstract Object getObjectField(PersistenceCapable pc,
                                          int field, Object currentValue);

    public final Object getObjectField(PersistenceCapable pc, int field) {
        return getObjectField(pc, field, null);
    }

    public final boolean getBooleanField(PersistenceCapable pc, int field,
                                         boolean currentValue) {
        return ((Boolean) getObjectField(pc, field, null)).booleanValue();
    }

    public final byte getByteField(PersistenceCapable pc, int field,
                                   byte currentValue) {
        return ((Byte) getObjectField(pc, field, null)).byteValue();
    }

    public final char getCharField(PersistenceCapable pc, int field,
                                   char currentValue) {
        return ((Character) getObjectField(pc, field, null)).charValue();
    }

    public final double getDoubleField(PersistenceCapable pc, int field,
                                       double currentValue) {
        return ((Double) getObjectField(pc, field, null)).doubleValue();
    }

    public final float getFloatField(PersistenceCapable pc, int field,
                                     float currentValue) {
        return ((Float) getObjectField(pc, field, null)).floatValue();
    }

    public final int getIntField(PersistenceCapable pc, int field,
                                 int currentValue) {
        return ((Integer) getObjectField(pc, field, null)).intValue();
    }

    public final long getLongField(PersistenceCapable pc, int field,
                                   long currentValue) {
        return ((Long) getObjectField(pc, field, null)).longValue();
    }

    public final short getShortField(PersistenceCapable pc, int field,
                                     short currentValue) {
        return ((Short) getObjectField(pc, field, null)).shortValue();
    }

    public final String getStringField(PersistenceCapable pc, int field,
                                       String currentValue) {
        return (String) getObjectField(pc, field, null);
    }

    public abstract Object getTransactionalObjectId(PersistenceCapable pc);

    public abstract boolean isDeleted(PersistenceCapable pc);

    public abstract boolean isDirty(PersistenceCapable pc);

    public abstract boolean isLoaded(PersistenceCapable pc, int field);

    public abstract boolean isNew(PersistenceCapable pc);

    public abstract boolean isPersistent(PersistenceCapable pc);

    public abstract boolean isTransactional(PersistenceCapable pc);

    public abstract void makeDirty(PersistenceCapable pc, String fieldName);

    public abstract void preSerialize(PersistenceCapable pc);

    /**
     * Requests and returns the value of the specified field from the
     * specified PersistenceCapable instance.
     */
    protected final Object provideField(PersistenceCapable pc, int field) {
        try {
            m_tmpValue = null;
            pc.jdoProvideField(field);
            return m_tmpValue;
        } finally {
            m_tmpValue = null;
        }
    }

    public final void providedObjectField(PersistenceCapable pc, int field,
                                          Object currentValue) {
        m_tmpValue = currentValue;
    }

    public final void providedBooleanField(PersistenceCapable pc, int field,
                                           boolean currentValue) {
        providedObjectField
            (pc, field, currentValue ? Boolean.TRUE : Boolean.FALSE);
    }

    public final void providedByteField(PersistenceCapable pc, int field,
                                        byte currentValue) {
        providedObjectField(pc, field, new Byte(currentValue));
    }

    public final void providedCharField(PersistenceCapable pc, int field,
                                        char currentValue) {
        providedObjectField(pc, field, new Character(currentValue));
    }

    public final void providedDoubleField(PersistenceCapable pc, int field,
                                          double currentValue) {
        providedObjectField(pc, field, new Double(currentValue));
    }

    public final void providedFloatField(PersistenceCapable pc, int field,
                                         float currentValue) {
        providedObjectField(pc, field, new Float(currentValue));
    }

    public final void providedIntField(PersistenceCapable pc, int field,
                                       int currentValue) {
        providedObjectField(pc, field, new Integer(currentValue));
    }

    public final void providedLongField(PersistenceCapable pc, int field,
                                        long currentValue) {
        providedObjectField(pc, field, new Long(currentValue));
    }

    public final void providedShortField(PersistenceCapable pc, int field,
                                         short currentValue) {
        providedObjectField(pc, field, new Short(currentValue));
    }

    public final void providedStringField(PersistenceCapable pc, int field,
                                          String currentValue) {
        providedObjectField(pc, field, currentValue);
    }

    /**
     * Directs the specified PersistenceCapable instance to update jdoFlags
     * to the specified value.
     */
    protected final void replaceFlags(PersistenceCapable pc, byte flags) {
        try {
            m_tmpByte = flags;
            pc.jdoReplaceFlags();
        } finally {
            m_tmpByte = 0x0;
        }
    }

    public final byte replacingFlags(PersistenceCapable pc) {
        return m_tmpByte;
    }

    /**
     * Directs the specified PersistenceCapable instance to replace the
     * specified field with the specified value.
     */
    protected final void replaceField(PersistenceCapable pc, int field,
                                      Object value) {
        try {
            m_tmpValue = value;
            pc.jdoReplaceField(field);
        } finally {
            m_tmpValue = null;
        }
    }

    public final Object replacingObjectField(PersistenceCapable pc,
                                             int field) {
        return m_tmpValue;
    }

    public final boolean replacingBooleanField(PersistenceCapable pc,
                                               int field) {
        return ((Boolean) replacingObjectField(pc, field)).booleanValue();
    }

    public final byte replacingByteField(PersistenceCapable pc,
                                         int field) {
        return ((Byte) replacingObjectField(pc, field)).byteValue();
    }

    public final char replacingCharField(PersistenceCapable pc,
                                         int field) {
        return ((Character) replacingObjectField(pc, field)).charValue();
    }

    public final double replacingDoubleField(PersistenceCapable pc,
                                             int field) {
        return ((Double) replacingObjectField(pc, field)).doubleValue();
    }

    public final float replacingFloatField(PersistenceCapable pc,
                                           int field) {
        return ((Float) replacingObjectField(pc, field)).floatValue();
    }

    public final int replacingIntField(PersistenceCapable pc,
                                       int field) {
        return ((Integer) replacingObjectField(pc, field)).intValue();
    }

    public final long replacingLongField(PersistenceCapable pc,
                                         int field) {
        return ((Long) replacingObjectField(pc, field)).longValue();
    }

    public final short replacingShortField(PersistenceCapable pc,
                                           int field) {
        return ((Short) replacingObjectField(pc, field)).shortValue();
    }

    public final String replacingStringField(PersistenceCapable pc,
                                             int field) {
        return (String) replacingObjectField(pc, field);
    }

    public abstract StateManager replacingStateManager(PersistenceCapable pc,
                                                       StateManager sm);

    public abstract void setObjectField(PersistenceCapable pc, int field,
                                        Object currentValue, Object newValue);

    public final void setBooleanField(PersistenceCapable pc, int field,
                                      boolean currentValue, boolean newValue) {
        setObjectField
            (pc, field, (currentValue ? Boolean.TRUE : Boolean.FALSE),
             (newValue ? Boolean.TRUE : Boolean.FALSE));
    }

    public final void setByteField(PersistenceCapable pc, int field,
                                   byte currentValue, byte newValue) {
        setObjectField
            (pc, field, new Byte(currentValue), new Byte(newValue));
    }

    public final void setCharField(PersistenceCapable pc, int field,
                                   char currentValue, char newValue) {
        setObjectField
            (pc, field, new Character(currentValue), new Character(newValue));
    }

    public final void setDoubleField(PersistenceCapable pc, int field,
                                     double currentValue, double newValue) {
        setObjectField
            (pc, field, new Double(currentValue), new Double(newValue));
    }

    public final void setFloatField(PersistenceCapable pc, int field,
                                    float currentValue, float newValue) {
        setObjectField
            (pc, field, new Float(currentValue), new Float(newValue));
    }

    public final void setIntField(PersistenceCapable pc, int field,
                                  int currentValue, int newValue) {
        setObjectField
            (pc, field, new Integer(currentValue), new Integer(newValue));
    }

    public final void setLongField(PersistenceCapable pc, int field,
                                   long currentValue, long newValue) {
        setObjectField(pc, field, new Long(currentValue), new Long(newValue));
    }

    public final void setShortField(PersistenceCapable pc, int field,
                                    short currentValue, short newValue) {
        setObjectField
            (pc, field, new Short(currentValue), new Short(newValue));
    }

    public final void setStringField(PersistenceCapable pc, int field,
                                     String currentValue, String newValue) {
        setObjectField(pc, field, currentValue, newValue);
    }
}
