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

/** 
 * Helper class for fetching remote URLs. Provides a pool of worker threads
 * that actually fetch the URLs, thus enabling the URL connections to be
 * interrupted early if the remote server hangs or doesn't respond.
 *
 * @author Dirk Gomez
*/

import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class URLPool {

    private static final Logger s_log = Logger.getLogger(URLPool.class);

    private static String m_data;
    private static String m_url;

    private int m_currentPoolSize = 0;
    private int m_poolSize;
    private long m_timeOut;

    /**
     * Create a new URLPool with a default poolsize of 10 and a default
     * timeout of 4 seconds.
     *
     */
    public URLPool() {
      this(10, 4000);
    }
    
    /**
     * Create a new URLPool with a default timeout of 4 seconds.
     *
     * @param poolsize - maximum number of threads allowed to be running at
     * any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     */
    public URLPool(int poolsize) {
      this(poolsize, 4000);
    }
    
    /**
     * Create a new URLPool with a default timeout of 4 seconds.
     *
     * @param poolsize - maximum number of threads allowed to be running at
     * any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @param timeout - timeout in milliseconds to use when fetching URLs to
     * prevent a slow remote server from delaying the calling application
     * indefinitely.
     */
    public URLPool(int poolsize, long timeout) {
        m_poolSize = poolsize;
        m_timeOut = timeout;
    }


    /**
     * Returns the pool size - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     *
     * @return the pool size - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     */
    public int getPoolSize() {
        return m_poolSize;
    };

    /**
     * Sets the pool size - maximum number of threads allowed to be running
     * at any given time, any subsequent requests for urls are queued until a
     * thread becomes available.
     * @param s_poolSize the maximum number of threads allowed to be running
     * at any given time
     */
    public void setPoolSize(int s_poolSize) {
        m_poolSize = s_poolSize;
    };

    /**
    * Returns the timeout to use when fetching URLs to prevent a slow remote
    * server from delaying the calling application indefinitely.
    *
    * @return the timeout to use when fetching URLs to prevent a slow remote
    * server from delaying the calling application indefinitely.
    */
    public long getTimeOut() {
        return m_timeOut;
    };

    /**
    * Sets the timeout to use when fetching URLs to prevent a slow remote
    * server from delaying the calling application indefinitely.
    *
    * @param s_timeOut the timeout to use when fetching URLs to prevent a slow
    * remote server from delaying the calling application indefinitely.
    */
    public void setTimeOut(long s_timeOut) {
        m_timeOut = s_timeOut;
    };

    /**
     * fetches the remote URL, returning the data from the page, or null if an
     * error occurred.
     *
     * @param URL fetches the remote URL, returning the data from the page, or
     * null if an error occurred.
     */
    public String fetchURL(String URL) {
        // Check whether there is a "slot" available to fetch the URL
        // If not then sleep for half the fetch timeout time.
        while (m_currentPoolSize > m_poolSize) {
            try {
                Thread.sleep(m_timeOut/2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        URLFetcher fetcher = new URLFetcher(URL);
        s_log.debug("Starting fetch thread for " + URL);
        m_currentPoolSize++;
        fetcher.start();
        try {
            fetcher.join(m_timeOut);
            if (fetcher.isAlive()) {
                s_log.debug("Thread still alive after " + m_timeOut + " milliseconds " + URL);
                fetcher.interrupt();
                fetcher.join();
            }
            s_log.debug("Joined with thread " + URL);
        } catch (InterruptedException ex) {
            s_log.debug("URL Fetcher interrupted", ex);
        }
        m_currentPoolSize--;
        return fetcher.getData();
    }

    private static class URLFetcher extends Thread {
        private String m_data;
        private String m_url;

        public URLFetcher(String url) {
            m_data = null;
            m_url = url;
        }
        
        public String getData() {
            return m_data;
        }

        public void run() {
            StringBuffer buffer = new StringBuffer();
            // Let's be nice to the user. If there's no "://" in the string,
            // just prepend "http://"
            if (m_url.indexOf("://") == -1) {
                m_url = "http://" + m_url;
            }
            
            try {
                URL url = new URL(m_url);
                URLConnection con = url.openConnection();
               
                InputStream is = con.getInputStream();
                BufferedReader input = new BufferedReader(new InputStreamReader(is));
                
                String line;
                while ((line = input.readLine()) != null) {
                    buffer.append(line).append('\n');
                }
                input.close();
            } catch (MalformedURLException mal) {
                mal.printStackTrace();
                return;
            } catch (IOException io) {
                io.printStackTrace();
                return;
            }

            
            m_data = buffer.toString();
        }
    }

}
