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

package com.arsdigita.util.url;

import org.apache.log4j.Logger;

import com.arsdigita.util.url.URLCache;
import com.arsdigita.util.url.URLPool;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;

import java.util.HashMap;

/**
 * Provides the main public API to the URL service through a bunch of static
 * methods. Applications register services to define a certain set of
 * characteristics such as cache size, thread pool size, cache timeouts. There
 * is a default service for applications that don't have any special
 * requirements.
 *
 * @author Dirk Gomez
 */

public class URLFetcher {

    private static final Logger s_log = Logger.getLogger(URLFetcher.class);

    private static HashMap s_services = new HashMap();

    /**
     * Registers a new service the key is the unique name for the service,
     * typically the packagename of the application using the service. The
     * pool & cache parameters allow special characteristics to be defined for
     * the service, if null then default to the global service.
     * Cache information about failed URL retrievals.
    */

    public static void registerService(String key, URLPool pool, URLCache cache) {
        registerService(key, pool, cache, true);
    };

    /**
     * Registers a new service the key is the unique name for the service,
     * typically the packagename of the application using the service. The
     * pool & cache parameters allow special characteristics to be defined for
     * the service, if null then default to the global service.
     * @param key                   service key name
     * @param pool                  URLPool to be used
     * @param cache                 URLCache to be used
     * @param cacheFailedRetrievals determine whether information on failed
     *                              retrievals should be cached
    */

    public static void registerService(String key, URLPool pool, URLCache cache, boolean cacheFailedRetrievals) {
        Assert.assertTrue(!StringUtils.emptyString(key), "Key must not be empty!");
        CacheService cs = new CacheService(pool, cache, cacheFailedRetrievals);
        s_services.put(key, cs);
    };

    /**
     * Fetches the URL using the service specified by the key param. Looks in
     * the cache for the url, if not present fetches the url & stores it in
     * the cache.Returns the data for the page, or null if the fetch failed.
     *
     * @deprecated use {@link #fetchURLData(String url, String key)} instead
    */
    public static String fetchURL(String url, String key) {
        URLData data = fetchURLData(url, key);
        if (data != null) {
            return data.getContentAsString();
        }
        return null;
    }


    /**
     * Fetches the URL using the service specified by the key param. Looks in
     * the cache for the url, if not present fetches the url & stores it in
     * the cache.Returns the data for the page, or null if the fetch failed.
    */
    public static URLData fetchURLData(String url, String key) {
        Assert.assertTrue(!StringUtils.emptyString(url), 
                          "URL must not be empty!");

        CacheService cs = getService(key);

        URLData urlData = cs.cache.retrieveData(url);
        if (urlData == null) {
            urlData = cs.pool.fetchURLData(url);
            if (urlData != null && urlData.getContent() != null &&
                urlData.getContent().length > 0) {
                cs.cache.store(url,urlData);
            } else {
                if (cs.cacheFailedRetrievals == true) {
                    cs.cache.store(url,urlData);
                }
            }
            return urlData;
        } else {
            return urlData;
        }
    };


    /**
     * Purges the specified URL from the cache.
     */
    public static void purgeURL(String url, String key) {
        Assert.assertTrue(!StringUtils.emptyString(url), "URL must not be null!");
        CacheService cs = getService(key);
        cs.cache.purge(url);
    };

    public boolean hasService(String key) {
        return getService(key) != null;
    }

    private static CacheService getService(String key) {
        Assert.assertTrue(!StringUtils.emptyString(key), "Key must not be empty!");
        CacheService cs = (CacheService) s_services.get(key);
        return cs;
    }
    /**
     * A registered Cacheservice.
     */
    private static final class CacheService {
        final URLPool pool;
        final URLCache cache;
        final boolean cacheFailedRetrievals;

        CacheService (URLPool pool, URLCache cache, boolean cacheFailedRetrievals) {
            Assert.assertNotNull(pool, "URLPool cannot be null!");
            Assert.assertNotNull(cache, "URLCache cannot be null!");

            this.pool = pool;
            this.cache = cache;
            this.cacheFailedRetrievals = cacheFailedRetrievals;
        }
    }
}
