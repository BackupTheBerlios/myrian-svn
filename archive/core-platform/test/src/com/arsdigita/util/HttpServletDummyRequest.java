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

import java.util.LinkedList;
import javax.servlet.http.*;
import javax.servlet.ServletContext;
import java.util.*;
import java.io.*;
import javax.servlet.*;

/**
   Dummy request object for unit testing of form methods that include
   requests in their signatures.
*/

public class HttpServletDummyRequest implements HttpServletRequest {
    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/HttpServletDummyRequest.java#2 $ by $Author: dennis $, $DateTime: 2002/06/27 18:19:33 $";

    private HashMap parameters;
    private HashMap attributes;
    private String m_url;
    private String m_queryString;
    
    public HttpServletDummyRequest() {
        this(true);
    }

    public HttpServletDummyRequest(boolean isDebug) {
        parameters=new HashMap(); 
        attributes=new HashMap();
    }
  
    public java.lang.Object getAttribute(java.lang.String name) { 
        return attributes.get(name); 
    }
  
    public java.util.Enumeration getAttributeNames() { 
        return getNamesHelper(attributes);
    }

    public java.lang.String getParameter(java.lang.String name) { 
        LinkedList valuesList= (LinkedList)(parameters.get(name));
        return ((valuesList!=null)? (String)(valuesList.getFirst()) : null);
    }
  
    public java.util.Enumeration getParameterNames() {
        return getNamesHelper(parameters);
    }
  
    public java.lang.String[] getParameterValues(java.lang.String name) {
        LinkedList valuesList = (LinkedList)(parameters.get(name));
        if (valuesList != null) { 
            /*
              this annoying loop is because we 
              cannot directly cast Object[] to String[]
            */
            Object[] _objectArray=valuesList.toArray();
            String [] _stringArray=new String[_objectArray.length];
            for (int i=0;i<_objectArray.length;i+=1) {
                _stringArray[i]=(String)_objectArray[i];
            }
            return _stringArray;
        } else {
            return null;
        }
    }

    /**
     * sets the requestURI + query string
     */
    public void setURL(String s) { 
        int i = s.indexOf('?') ;
        if (i >= 0) { 
            m_url = s.substring(0, i);
            m_queryString = s.substring(i + 1);
        } else {
            m_url = s;
        }
    }
  
    /*
    naming convention here may seem odd.
    we keep it as setParameterValues rather than setParameterValue
    since we are appending to the list of values rather than overwriting it
  */
    public void setParameterValues(String name, String value) {
        LinkedList valuesList = (LinkedList)parameters.get(name);
        if (valuesList==null) {
            valuesList=new LinkedList();
        }
        if (value!=null) {
            valuesList.add(value);
        }
        parameters.put(name,valuesList);
    }
  
    public void setParameterValues(String name, String[] values) {
        if (values.length==0) {
            return;
        }
        LinkedList valuesList = (LinkedList)parameters.get(name);
        if (valuesList==null) {
            valuesList=new LinkedList();
        }
        for (int i=0;i<values.length;i+=1) {
            if (values[i]!=null) {
                valuesList.add(values[i]);
            }
        }
        parameters.put(name,valuesList);
    }
    public Object removeParameterValue(String name) {
        return parameters.remove(name);
    }


    private java.util.Enumeration getNamesHelper(HashMap h) { 
        Set variableNamesSet = h.keySet();
        Iterator variableNamesIterator;
        Vector temporary = new Vector();
        if (variableNamesSet!=null) {
            variableNamesIterator= variableNamesSet.iterator();
            while (variableNamesIterator.hasNext()) {
                temporary.add(variableNamesIterator.next());
            }
        }
        return temporary.elements();
    }

    public java.lang.String getAuthType() { return null; }
  
    public Cookie[] getCookies() { return null; }
  
    public long getDateHeader(java.lang.String name) { return (long)0; }
  
    public java.lang.String getHeader(java.lang.String name) { return null; }
  
    public java.util.Enumeration getHeaders(java.lang.String name) { return null; }
  
    public java.util.Enumeration getHeaderNames() { return null; }
  
    public int getIntHeader(java.lang.String name) { return 0; }
  
    public java.lang.String getMethod() { return null; }
  
    public java.lang.String getPathInfo() { return null; }
  
    public java.lang.String getPathTranslated() { return null; }
  
    public java.lang.String getContextPath() { return null; }
  
    public void setQueryString(String s) { 
        m_queryString = s;
    }
    
    public java.lang.String getQueryString() { 
        return m_queryString;
    }
  
    public java.lang.String getRemoteUser() { return null; }
  
    public boolean isUserInRole(java.lang.String role) { return false; }
  
    public java.security.Principal getUserPrincipal() { return null; }
  
    public java.lang.String getRequestedSessionId() { return null; }
  
    public void setRequestURI(String s) { 
        m_url = s;
    }

    public java.lang.String getRequestURI() { 
        return m_url;
    }
  
    public java.lang.String getServletPath() { return null; }
  
    public HttpSession getSession(boolean create) { return null; }
  
    public HttpSession getSession() { return null; }
  
    public boolean isRequestedSessionIdValid() { return true; }
  
    public boolean isRequestedSessionIdFromCookie() { return true; }
  
    public boolean isRequestedSessionIdFromURL() { return true; }
  
    public boolean isRequestedSessionIdFromUrl() { return true; }
  
    //methods for ServletRequest Interface
  
    public java.lang.String getCharacterEncoding() { return null; }
  
    public int getContentLength() { return 0; }
  
    public java.lang.String getContentType() { return null; }
  
    public ServletInputStream getInputStream() 
        throws java.io.IOException { return null; }
           
  
    public java.lang.String getProtocol() { return null; }
  
    public java.lang.String getScheme() { return null; }
  
    public java.lang.String getServerName() { return null; }

    public int getServerPort() { return 0; }
  
    public java.io.BufferedReader getReader() 
        throws java.io.IOException { return null; }
  
    public java.lang.String getRemoteAddr() { return null; }
  
    public java.lang.String getRemoteHost() { return null; }
  
    public void setAttribute(java.lang.String name,
                             java.lang.Object o) { return; }
  
    public void removeAttribute(java.lang.String name) { return; }
  
    public java.util.Locale getLocale() { return Locale.ENGLISH; }
  
    public java.util.Enumeration getLocales() { return null; }

    public boolean isSecure() { return true; }

    public RequestDispatcher getRequestDispatcher(java.lang.String path) { 
        return null; 
    }

    public java.lang.String getRealPath(java.lang.String path) { return null; }
    public Map getParameterMap() {
        return parameters;
    }
 
    public StringBuffer getRequestURL() { 
        return null; 
    }
   
    public void setCharacterEncoding(String env)
    throws UnsupportedEncodingException {
    }

}





