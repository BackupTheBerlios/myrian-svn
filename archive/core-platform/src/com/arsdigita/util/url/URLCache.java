/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util.url;
import org.apache.log4j.Logger;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper class for caching fetched URLs. A more advanced implementation of
 * com.arsdigita.util.Cache which rather than being capacity limited in terms
 * of number of stored keys, it is limited according to memory usage.
*/

public class  URLCache {

    private static final Logger s_log = Logger.getLogger(URLCache.class);

    private HashMap map = new HashMap ();
    private long m_maxSize;
    private long m_defaultExpiryTime;
    private long m_curSize = 0;

    /**
     * Create a new URLCache. If more elements are put into the cache than it
     * can hold, expired items will be evicted.
     *
     */
    public URLCache() {
      this(10000000, 0);
    }
    /**
     * Create a new URLCache with a maximum size of size characters.  If more
     * elements are put into the cache than it can hold, expired items will
     * be evicted. 
     *
     * @param size cache size in characters
     */
    public URLCache(long size) {
        this(size, 15*60*1000);
    }

    /**
     * Create a new URLCache with a maximum size of size characters.  If more
     * elements are put into the cache than it can hold, expired items will
     * be evicted. 
     * <p>
     * Also allows an expiration time to be set; items in the cache that are
     * older than that time will be evicted. 
     *
     * @param size cache size in characters
     * @param default expiry time default expiry time for cached items. When
     * retrieving an item, if its age exceeds expiry time, then it will be 
     * discarded.
     */
    public URLCache(long size, long expiryTime) {
        this.m_maxSize = size;
        this.m_defaultExpiryTime = expiryTime;
    }


    /**
     * Returns max size - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, expired items are evicted until there is
     * sufficient space for the new item. If this is not enough, items will be
     * randomly evicted.
     *
     * @return max size - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, least recently used items are evicted until
     * there is sufficient space for the new item.
     */
    public long getMaxSize() {
        return m_maxSize;
    };

    /**
     * Sets max size - Maximum memory usage allowed for the data stored in the
     * cache. When exceeded, expired items are evicted until there is
     * sufficient space for the new item. If this is not enough, items will be
     * randomly evicted
     *
     * @param s_maxSize - Maximum memory usage allowed for the data stored in
     * the cache. When exceeded, least recently used items are evicted until
     * there is sufficient space for the new item.
     */
    public void setMaxSize(long s_maxSize) {
        m_maxSize = s_maxSize;
    };

    /**
    * Returns default expiry time for cached items. When retrieving an item,
    * if its age exceeds expiry time, then it will be discarded.
    *
    * @return expiry time - default expiry time for cached items. When
    * retrieving an item, if its age exceeds expiry time, then it will be
    * discarded. Default expiry time is 15*60*1000.
    */
    public long getDefaultExpiryTime() {
        return m_defaultExpiryTime;
    };

    /**
    * Sets default expiry time for cached items. When retrieving an item, if
    * its age exceeds expiry time, then it will be discarded.
    *
    * @param s_DefaultExpiryTime - default expiry time for cached items. When
    * retrieving an item, if its age exceeds expiry time, then it will be
    * discarded. Default expiry time is 15*60*1000.
    */
    public void setDefaultExpiryTime(long s_DefaultExpiryTime) {
        m_defaultExpiryTime = s_DefaultExpiryTime;
    };

    /**
    *  Stores data for a url in the cache. Expiry time is the default expiry
    *  time.
    *
    * @param url - URL to be stored in the cache.
    * @param data - data to be stored in the cache
    */
    public synchronized void store(String url, String data) {
        store (url, data, m_defaultExpiryTime);
    };


    /**
    *  Stores data for a url in the cache.
    *
    * @param url - URL to be stored in the cache.
    * @param data - data to be stored in the cache
    * @param expiry - expiry time in milliseconds.
    */
    public synchronized void store(String url, String data, long expiry) {
        s_log.debug("Storing location URL " + url + " in the URLCache.");
        Entry e = new Entry(data, System.currentTimeMillis(), expiry);

        // Check whether we have hit the max size.
        // If so, evict expired items as long as m_curSize is > m_maxSize.
        // If that is not enough, then just evict items.

        if (data != null) {
            m_curSize+=data.length()+url.length();
        } else {
            m_curSize+=url.length();
        }
        if (m_curSize >= m_maxSize) {
            Iterator iter = map.entrySet().iterator();
            String minUrl = null;
            long now = System.currentTimeMillis();

            while (m_curSize >= m_maxSize && iter.hasNext()) {
                Map.Entry ent = (Map.Entry)iter.next();
                Entry e2 = (Entry)map.get(ent.getKey());
                if (e2.expiry > 0 && e2.creationTime < now - e2.expiry) {
                    s_log.debug("Evicting " + ent.getKey() + " from the URLCache. (Expired)");
                    if (e2.data != null) {
                        m_curSize = m_curSize-((String) ent.getKey()).length()-e2.data.length();
                    } else {
                        m_curSize = m_curSize-((String) ent.getKey()).length();
                    }
                    iter.remove();
                }
            }
            if (iter.hasNext() == false && m_curSize >= m_maxSize) {
                iter = map.entrySet().iterator();
                while (m_curSize >= m_maxSize && iter.hasNext()) {
                    Map.Entry ent = (Map.Entry)iter.next();
                    Entry e2 = (Entry)map.get(ent.getKey());
                    s_log.debug("Evicting " + ent.getKey() + " from the URLCache. (Just evicting)");
                    if (e2.data != null) {
                        m_curSize = m_curSize-((String) ent.getKey()).length()-e2.data.length();
                    } else {
                        m_curSize = m_curSize-((String) ent.getKey()).length();
                    }
                    iter.remove();
                }
            }
        }
        map.put(url, e);
    };

    /**
     * removes a url from the cache
     */
    public  synchronized void purge(String url) {
        s_log.debug("Evicting " + url + " from the URLCache.");
        Entry e = (Entry)map.get(url);
        if (e != null) {
            if (e.data != null) {
                m_curSize = m_curSize-url.length()-e.data.length();
            } else {
                m_curSize = m_curSize-url.length();
            }
            map.remove(url);
        }
    };

    /**
     *retrieves a url from the cache, returning null if not present or it has
     *expired.
     */

    public synchronized String retrieve(String url) {
        s_log.debug("Trying to retrieve " + url + " from the URLCache.");
        Entry e = (Entry)map.get(url);
        if (e == null) return null;
        // make sure the item hasn't expired
        if (e.expiry > 0) {
            long now = System.currentTimeMillis();
            if (e.creationTime < now - e.expiry) {
                // put in cache more than maxAge ms ago, so remove it
                purge(url);
                // and pretend we never saw it
                return null;
            }
        }
        e.lastUse = System.currentTimeMillis();
        s_log.debug("URL " + url + " is in the cache.");
        return e.data;
    };
    
    /**
     * A single entry in the Cache.  Contains the actual object of
     * interest and the last time the object's been looked up; also
     * contains the object's creation time.
     */
    private  class Entry {
        String data;
        long lastUse;
        long creationTime;
        long expiry;

        Entry (String data, long lastUse, long expiry) {
            this.data = data;
            this.lastUse = lastUse;
            this.creationTime = System.currentTimeMillis();
            this.expiry = expiry;
        }
    }

}
