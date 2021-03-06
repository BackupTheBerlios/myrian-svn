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

/**
 * A class that holds the information about a fetched URL including the
 * actual data and all of the headers.  
 *
 * @author Randy Graebner
 */

import java.net.URLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.arsdigita.util.StringUtils;
import com.arsdigita.util.Assert;

public class URLData {

    private static final Logger s_log = Logger.getLogger(URLData.class);
    private String m_url;
    private Map m_headers;
    private byte[] m_content;
    private Exception m_exception;

    // this is the data read in from the connection using its best guess
    // as to what the character encoding is.  
    // This should be @deprected with people using m_content instead
    private String m_data;
    

    /**
     * Create a new URLData object with the given URL
     *
     */
    public URLData(String url) {
        this(url, null, null);
    }

    /**
     * Create a new URLData the given URL, headers, and content
     *
     * @pre url != null
     */
    public URLData(String url, Map headers, byte[] content) {
        Assert.assertNotNull(url);
        m_url = null;
        setHeaders(headers);
        setContent(content);
    }

    /**
     *  This returns the actual URL that is represented by the data
     */  
    public String getURL() {
        return m_url;
    }

    /**
     *  This returns any headers that were returned when the connection
     *  was opened to the URL
     */
    public Map getHeaders() {
        return m_headers;
    }

    public void setHeaders(Map headers) {
        m_headers = headers;
    }
    
    /**
     *  This is a byte array representation of the content returned by 
     *  the connection. This returns an empty array of length zero
     *  if there is no content.
     */
    public byte[] getContent() {
        if (m_content == null) {
            m_content = new byte[0];
        }
        return m_content;
    }

    public void setContent(byte[] content) {
        m_content = content;
    }


    /**
     *  This is a convenience method that returns the content as a 
     *  String in using the best encoding that it is able to guess.
     *  @deprecated use getContent() and then use the appropriate
     *  charset to do the converstion.  This also only works when
     *  the content is text and there are no guarantees when the
     *  returned content is actually binary.  
     */
    public String getContentAsString() {
        String contentType = getContentType();
        
        // if no explicit encoding then we should read the
        // first 4 bytes of the document to look for an XML
        // byte order mark & if found, then extract the charset.
        String encoding = "ISO-8859-1";
        if (contentType != null) {
            int offset = contentType.indexOf("charset=");
            if (offset != -1) {
                encoding = contentType.substring(offset + 8).trim();
            }
        }
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Received content type " + getContentType());
        }

        try {
            return new String(getContent(), encoding);
        } catch (UnsupportedEncodingException e) {
            s_log.warn("Encoding " + encoding + " not supported.", e);
            return new String(getContent());
        }
    }


    /**
     *  This is the exception that was raised while trying to connect
     *  to the URL or read data from the url
     */
    public void setException(Exception e) {
        m_exception = e;
    }

    /**
     *  this returns any exception that was raised while downloading
     *  or reading the URL.  This will return null when no exception was 
     *  raised.
     */
    public Exception getException() {
        return m_exception;
    }

    /// 
    // Some convenience methods for getting various headers
    ///

    /**
     *  this returns the content type after the headers have been set.
     *  This is merely a convenience method
     */
    public String getContentType() {
        if (getHeaders() != null) {
            return (String)getHeaders().get("content-type");
        } else {
            return null;
        }
    }
}
