/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * data structure for a fixed-size cache table.  Note that Cache is
 * not thread-safe; it is up to the caller to synchronize if a cache
 * is shared across multiple threads.  Also includes a static global
 * cache, whose methods <em>are</em> threadsafe.
 *
 * @author Bill Schneider 
 * @version $Revision: #1 $, $Date: 2003/12/10 $
 */

public class Cache {

    public static final String versionId = "$Id: //core-platform/test-qgen/src/com/arsdigita/util/Cache.java#1 $ by $Author: dennis $, $DateTime: 2003/12/10 16:59:20 $";

    // map keys to their values
    private static Cache s_instance = new Cache(32000);

    private HashMap m_map;
    private long m_maxSize;
    private long m_curSize;
    private long m_maxAge;

    /**
     * Create a new Cache of a fixed size.  If more elements are put into
     * the cache than it can hold, the least-recently used item will
     * be evicted.
     *
     * @param size the number of items to allow before eviction
     */
    public Cache(long size) {
        this(size, 0);
    }

    /**
     * Create a new Cache of a fixed size.  If more elements are put into
     * the cache than it can hold, the least-recently used item will be
     * evicted.
     * <p>
     * Also allows an expiration time to be set; items in the cache
     * that are older than that time will be evicted.
     *
     * @param size the number of items to allow before eviction
     * @param maxAge the longest period of time, in milliseconds, that
     * an element may stay in the cache before it is evicted, by default.
     * (may be overriden by put).
     */
    public Cache(long size, long maxAge) {
        m_map = new HashMap();
        m_maxSize = size;
        m_curSize = 0;
        m_maxAge = maxAge;
    }

    /**
     * Puts a new key/value pair into the cache with default lifetime
     * (this.maxAge).
     * @param key the key
     * @param value the value
     */
    public void put(Object key, Object value) {
        put(key, value, m_maxAge);
    }

    /**
     * Puts a new key/value pair into the cache.
     * @param key the key
     * @param value the value
     * @param maxAge the maximum lifetime of this cache entry, in
     * milliseconds
     */
    public void put(Object key, Object value, long maxAge) {
        Entry e = new Entry(value, System.currentTimeMillis(), maxAge);
        if (m_curSize >= m_maxSize) {
            // have to evict something... find the least
            // recently used item
            Iterator iter = m_map.entrySet().iterator();
            long min = Long.MAX_VALUE;
            Object minKey = null;
            while (iter.hasNext()) {
                Map.Entry ent = (Map.Entry)iter.next();
                Entry e2 = (Entry)m_map.get(ent.getKey());
                long now = System.currentTimeMillis();
                if (e2.m_lastUse < min ||
                    (e2.m_maxAge > 0 && e2.m_creationTime < now - e2.m_maxAge)) {
                    min = e2.m_lastUse;
                    minKey = ent.getKey();
                }
            }
            m_map.remove(minKey);
            m_curSize--;
        }
        m_curSize++;
        m_map.put(key, e);
    }

    /**
     * Returns an object from the cache, if it exists and hasn't expired.
     * Returns null otherwise.
     * @param key the key to look up
     * @return the object mapped by <code>key</code>, or null
     */
    public Object get(Object key) {
        Entry e = (Entry)m_map.get(key);

        if (e == null) {
            return null;
        }

        // make sure the item hasn't expired
        if (m_maxAge > 0) {
            long now = System.currentTimeMillis();
            if (e.m_creationTime < now - m_maxAge) {
                // put in cache more than maxAge ms ago, so remove it
                m_map.remove(key);
                // and pretend we never saw it
                return null;
            }
        }
        e.m_lastUse = System.currentTimeMillis();
        return e.m_o;
    }

    
    /**
     * Removes the mapping for this key if it exists
     * @param key key whose mapping is to be removed
     */
    public void remove(Object key) {
        m_map.remove(key);
    }

    /**
     * Removes all mapping from this map
     */
    public void clear() {
        m_map.clear();
    }

    /**
     * Puts a new key/value pair into the static global cache with default
     * lifetime.
     * @param key the key
     * @param value the value
     */
    public static synchronized void putGlobal(Object key, Object value) {
        s_instance.put(key, value, s_instance.m_maxAge);
    }

    /**
     * Puts a new key/value pair into the static global cache with specified
     * lifetime.
     * @param key the key
     * @param value the value
     * @param maxAge the lifetime of this cache entry in ms
     */
    public static synchronized void putGlobal(Object key,
                                              Object value,
                                              long maxAge) {
        s_instance.put(key, value, maxAge);
    }

    /**
     * Removes the mapping for this key if it exists
     * @param key key whose mapping is to be removed
     */
    public static synchronized void removeGlobal(Object key) {
        s_instance.remove(key);
    }

    /**
     * Returns an object from the global cache, if it exists and hasn't
     * expired.
     * Returns null otherwise.
     * @param key the key to look up
     * @return the object mapped by <code>key</code>, or null
     */
    public static synchronized Object getGlobal(Object key) {
        return s_instance.get(key);
    }

    /**
     * A single entry in the Cache.  Contains the actual object of
     * interest and the last time the object's been looked up; also
     * contains the object's creation time.
     */
    private static class Entry {
        Object m_o;
        long m_lastUse;
        long m_creationTime;
        long m_maxAge;

        Entry(Object o, long lastUse, long maxAge) {
            m_o = o;
            m_lastUse = lastUse;
            m_creationTime = System.currentTimeMillis();
            m_maxAge = maxAge;
        }
    }

}
