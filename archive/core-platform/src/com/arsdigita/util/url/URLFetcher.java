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

import com.arsdigita.util.url.URLCache;
import com.arsdigita.util.url.URLPool;
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

    private static final Logger s_log = Logger.getLogger(URLPool.class);

    private static HashMap services = new HashMap();

    /**
     * Registers a new service the key is the unique name for the service,
     * typically the packagename of the application using the service. The
     * pool & cache parameters allow special characteristics to be defined for
     * the service, if null then default to the global service.
     * Cache information about failed URL retrievals.
    */

    public static void registerService(String key, URLPool pool, URLCache cache) {
        CacheService cs = new CacheService(pool, cache, true);
        services.put(key, cs);
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
        CacheService cs = new CacheService(pool, cache, cacheFailedRetrievals);
        services.put(key, cs);
    };
        
    /**
     * Fetches the URL using the service specified by the key param. Looks in
     * the cache for the url, if not present fetches the url & stores it in
     * the cache.Returns the data for the page, or null if the fetch failed.
    */
    public static String fetchURL(String url, String key) {
        CacheService cs = (CacheService) services.get(key);

        String urlData = ((URLCache) cs.cache).retrieve(url);
        if (urlData == null) {
            urlData = ((URLPool) cs.pool).fetchURL(url);
            if (urlData != null) {
                ((URLCache) cs.cache).store(url,urlData);
            } else {
                if (cs.cacheFailedRetrievals == true) {
                    ((URLCache) cs.cache).store(url,urlData);
                }
            }
            return urlData;
        } else {
            return urlData;
        }
    };


    /**
     *  Purges the specified URL from the cache. 
     */
    public static void purgeURL(String url, String key) {
        CacheService cs = (CacheService) services.get(key);
        ((URLCache) cs.cache).purge(url);
    };


    /**
     * A registered Cacheservice.
     */
    private static class CacheService {
        URLPool pool;
        URLCache cache;
        boolean cacheFailedRetrievals;

        CacheService (URLPool pool, URLCache cache, boolean cacheFailedRetrievals) {
            this.pool = pool;
            this.cache = cache;
            this.cacheFailedRetrievals = cacheFailedRetrievals;
        }
    }


}



