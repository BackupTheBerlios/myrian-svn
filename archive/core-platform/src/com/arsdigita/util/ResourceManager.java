/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;

/**
 * Wrapper for getResourceAsStream, so we can get file-based resources
 * with paths relative to the webapp root without needing a
 * ServletContext object.
 */

public class ResourceManager {

    private final static String CONFIGURE_MESSAGE =
        "Must configure ResourceManager by calling setWebappRoot or "
        + "setServletContext before use.";

    private static final Logger s_log =
        Logger.getLogger(ResourceManager.class);

    private static ResourceManager s_instance = new ResourceManager();

    private File m_webappRoot;
    private ServletContext m_servletContext;

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/ResourceManager.java#10 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

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
     * @exception java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public InputStream getResourceAsStream(String url) {
        if (m_webappRoot == null && m_servletContext == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        if (StringUtils.emptyString(url)) {
            throw new IllegalArgumentException("URL is empty: " + url);
        }
        if (m_servletContext != null) {
            // If we have a Servlet Context, use it.
            InputStream is = m_servletContext.getResourceAsStream(url);
            if (is == null) {
                String errorMessage = "Failed to retrieve resource: " + url +
                    "\nReal Path: " + m_servletContext.getRealPath(url);

                // Since this method is called during startup before the
                // log4j initializer has been run, we test to see whether to
                // log or print the error message.
                if (s_log.getAllAppenders().hasMoreElements()) {
                    s_log.error(errorMessage);
                } else {
                    System.err.println(errorMessage);
                }
            }
            return is;
        } else {
            try {
                return new FileInputStream(new File(m_webappRoot, url));
            } catch (FileNotFoundException fnfe) {
                String errorMessage = "Failed to retrieve resource: " + url +
                    "\nWebapp Root: " + m_webappRoot;

                if (s_log.getAllAppenders().hasMoreElements()) {
                    s_log.error(errorMessage);
                } else {
                    System.err.println(errorMessage);
                }
                return null;
            }
        }
    }

    /**
     * Gets the full path to a resource.
     *
     * Kinda hacky way of making sure that XML file loading works. Many Initializers load XML files from WEB-INF,
     * and had done so by calling ServletContext.getRealPath(). Problem is, under test, there is no ServletContext.
     * Swithcing to ResourceManager.getResourceAsStream works so long as there is no DTD. Calling this method gets
     * the correct path, and lets the parser properly load the file.
     *
     * Will probably remove when TestServletContext is always available.
     *
     * @param url
     * @return Full path
     */
    public String getResourcePath(String url) {
        if (m_webappRoot == null && m_servletContext == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        if (StringUtils.emptyString(url)) {
            throw new IllegalArgumentException("URL is empty: " + url);
        }

        if (m_servletContext != null) {
            return m_servletContext.getRealPath(url);
        } else {
            File f = new File(m_webappRoot, url);
            return f.getAbsolutePath();
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
     * @exception java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public File getResourceAsFile(String url) {
        if (m_servletContext == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        return new File( m_servletContext.getRealPath(url));
    }

    /**
     * Configures this ResourceManager to use the specified webapp
     * root.
     *
     * @param f the webapp root directory
     */
    public void setWebappRoot(File f) {
        m_webappRoot = f;
    }

    /**
     * Configures this ResourceManager to use the specified servlet
     * context.
     *
     * @param sctx the servlet context.
     */
    public void setServletContext(ServletContext sctx) {
        m_servletContext = sctx;
        m_webappRoot = new File(sctx.getRealPath("/"));
    }

    /**
     * returns the ServletContext that this ResourceManager uses
     *
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
     * @exception java.lang.IllegalStateException if class is
     * not configured prior to use.
     */
    public long getLastModified(String path) {
        if (m_webappRoot == null) {
            throw new IllegalStateException(CONFIGURE_MESSAGE);
        }
        return new File(m_webappRoot, path).lastModified();
    }
}
