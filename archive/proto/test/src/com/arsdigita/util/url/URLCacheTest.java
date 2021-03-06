package com.arsdigita.util.url;

import junit.framework.TestCase;
import com.arsdigita.util.StringUtils;

import java.util.Collection;

/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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


/**
 * URLCacheTest
 *
 */
public class URLCacheTest extends TestCase {
    private URLCache m_cache = new URLCache();

    public void testURLCache() throws Exception {
        URLCache cache;
        cache = new URLCache();

        cache = new URLCache(1);
        try {
            cache = new URLCache(0);
            fail("Should not be able to create cache with zero size!");
        } catch(Exception e) {
        }
        try {
            cache = new URLCache(-1);
            fail("Should not be able to create cache with negative size!");
        } catch(Exception e) {
        }

        cache = new URLCache(1, 0);
        try {
            cache = new URLCache(1, -1);
            fail("Should not be able to create cache with negative timeout");
        } catch(Exception e) {
        }

    }

    public void testStore() throws Exception {

        final String fooURL = "http://foo.com/";
        // Max cache size
        addToCache(fooURL, (int)(m_cache.getMaxSize() - fooURL.length()));
        purgeCache(fooURL);

        try {
            // Max cache size + 1
            int cachePlusOne = (int)(m_cache.getMaxSize() - fooURL.length()) + 1;
            addToCache(fooURL, cachePlusOne);
            fail("Should not be able to add an entry longer than the cache!");
        } catch(IllegalArgumentException e) {
            assertEquals("Cache should be empty!", 0, m_cache.getCurrentSize());
            assertNull("Data should be null!", m_cache.retrieve(fooURL));
        }

        // Test putting in two entries that just fill the cache.
        final String barURL = "http://bar.com/";
        final int dataSpace = (int)(m_cache.getMaxSize() - fooURL.length() - barURL.length());
        addToCache(fooURL, dataSpace/2);
        addToCache(barURL, dataSpace/2);

        assertInCache(fooURL);
        assertInCache(barURL);

        // Ensure Bar gets evicted
        addToCache(fooURL, dataSpace/2 + 1);
        TestCase.assertNull("Bar in cache!", m_cache.retrieve(barURL));
        purgeCache(fooURL);

        // Check that expired content is removed first.
        addToCache(fooURL, dataSpace/2, 1);
        addToCache(barURL, dataSpace/2);

        final String bazURL = "http://baz.com/";
        addToCache(bazURL, dataSpace/2);
        assertNull("Foo should be null!", m_cache.retrieve(fooURL));
        assertInCache(bazURL);
        assertInCache(barURL);

    }

    public void testPurge() throws Exception {
        final String url = "http://redhat.com/";
        addToCache(url, 10);
        purgeCache(url);
    }

    public void testRetrieve() throws Exception {
        final String url = "http://redhat.com/";
        addToCache(url, 10);
        purgeCache(url);
        // Don't use addToCache, as it immediately checks the cache,
        // and may fail with this short timeout
        m_cache.store(url, StringUtils.repeat('a', 10), 1);
        try {
            Thread.sleep(5);

        } catch(InterruptedException e) {

        }
        assertNull("Data should be null for " + url, m_cache.retrieve(url));


    }

    public void testSetMaxSize() throws Exception {
        final String fooURL = "http://foo.com/";
        final String barURL = "http://bar.com/";
        final int dataSpace = (int)(m_cache.getMaxSize() - fooURL.length() - barURL.length());
        addToCache(fooURL, dataSpace/2);
        addToCache(barURL, dataSpace/2);

        m_cache.setMaxSize(m_cache.getMaxSize() - 1);
        assertEquals("Current size is wrong!", dataSpace/2 + fooURL.length(), m_cache.getCurrentSize());
        Collection c = m_cache.getURLS();
        assertEquals("Cache should have one entry!", 1, c.size());

    }

    void addToCache(String url, int dataSize) {
        addToCache( url, dataSize, m_cache.getDefaultExpiryTime());
    }

    void addToCache(String url, int dataSize, long expiry) {
        String data = StringUtils.repeat('a', dataSize);
        m_cache.store(url, data);
        assertInCache(url);
    }

    void purgeCache( String url) {
        m_cache.purge(url);
        assertNull("Data should be null for " + url, m_cache.retrieve(url));
    }

    void assertInCache(String url) {
        assertNotNull(url + " not in Cache!", m_cache.retrieve(url));
    }


}
