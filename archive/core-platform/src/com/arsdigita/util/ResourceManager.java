/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.util;

import java.io.*;
import javax.servlet.ServletContext;
import org.apache.log4j.Category;

/**
 * Wrapper for getResourceAsStream, so we can get file-based resources
 * with paths relative to the webapp root without needing a
 * ServletContext object.
 */

public class ResourceManager {

    private final static String CONFIGURE_MESSAGE = 
        "Must configure ResourceManager by calling setWebappRoot or "
        + "setServletContext before use.";

    private static Category s_log = 
        Category.getInstance(ResourceManager.class);

    private static ResourceManager s_instance = new ResourceManager();
	
    private File m_webappRoot;
    private ServletContext m_servletContext;

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/ResourceManager.java#2 $ by $Author: dennis $, $DateTime: 2002/07/18 13:18:21 $";

    /**
     * Empty constructor, which we make private to enforce the singleton
     * pattern.
     */
    private ResourceManager() {
    }

    static {
        s_instance = new ResourceManager();
    }

    /**
     * implements singleton pattern
     * @return the instance 
     */
    public static ResourceManager getInstance() { 
        return s_instance;
    }

    /**
     * Returns a new InputStream object reading the URL argument.
     * Behaves similarly to ServletContext.getResourceAsStream(),
     * reading pathnames relative to the webapp root.
     *
     * @param url a URL interpreted as a pathname relative to the webapp root
     * @return a new input stream reading the named file, or null 
     * if not found
     * @exception throws java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public InputStream getResourceAsStream(String url) {
        if (m_webappRoot == null && m_servletContext == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        try {
            // TODO: if we have a servlet context, use it! 
            // Maybe we're in a WAR file, in which case we *have*
            // to use sctx.getResourceAsStream.
            if (m_servletContext != null) {
                try {
                    return m_servletContext.getResourceAsStream(url);
                } catch (NullPointerException e) {
                    s_log.warn("Failed to retrieve resource " + url + ".");
                    return null;
                }
            } else {
                return new FileInputStream(new File(m_webappRoot, url));
            }
        } catch (FileNotFoundException fnfe) { 
            return null;
        }
    }

    /**
     * Returns a new File object that refers to the URL argument.
     * Behaves similarly to getResourceAsStream(),
     * reading pathnames relative to the webapp root, except
     * we return a File object.
     *
     * @param url a URL interpreted as a pathname relative to the webapp root
     * @return a File object referring to the named resource, or null 
     * if not found
     * @exception throws java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public File getResourceAsFile(String url) { 
        if (m_servletContext == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        return new File( m_servletContext.getRealPath(url));
    }

    /**
     * configures this ResourceManager to use the webapp root 
     * named by {@param f}.
     * @param f the webapp root directory
     */
    public void setWebappRoot(File f) { 
        m_webappRoot = f;
    }

    /**
     * configures this ResourceManager to use the servlet context
     * {@param sctx}.
     * @param sctx the servlet context.
     */
    public void setServletContext(ServletContext sctx) {
        m_servletContext = sctx;
        m_webappRoot = new File(sctx.getRealPath("/"));
    }

    /**
     * returns the ServletContext that this ResourceManager uses
     * @return the servlet context
     */
    public ServletContext getServletContext() {
        return m_servletContext;
    }

    /**
     * @return the webapp root for this ResourceManager.
     */
    public File getWebappRoot() { 
        return m_webappRoot;
    }

    /**
     * Returns the last-modified time for the file on disk.
     *
     * @return the last-modified time for the file on disk.  Returns
     * 0L if this isn't available (within a WAR file, for example),
     * the file isn't found, or there's an I/O error.  This is consistent
     * with File.lastModified.
     * @exception throws java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public long getLastModified(String path) { 
        if (m_webappRoot == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        } 
        return new File(m_webappRoot, path).lastModified();
    }
}
