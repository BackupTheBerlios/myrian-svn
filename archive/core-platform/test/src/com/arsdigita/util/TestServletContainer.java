package com.arsdigita.util;

import org.apache.cactus.ServletURL;

import javax.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;
/*
* Created by IntelliJ IDEA.
* User: jorris
* Date: Oct 20, 2002
* Time: 4:50:01 PM
* To change this template use Options | File Templates.
*/
public class TestServletContainer {

   private HttpServletDummyRequest m_req;
   private HttpServletDummyResponse m_res;
   private HashMap m_servletMapping = new HashMap();
   private static Logger s_log = Logger.getLogger(TestServletContainer.class);

   public TestServletContainer(HttpServletDummyRequest req, HttpServletDummyResponse res) {
      m_req = req;
      m_res = res;

      m_req.setContainer(this);
      m_res.setContainer(this);
   }

   public void addServletMapping(String name, Servlet servlet) {
         m_servletMapping.put(name, servlet);
   }

   public void dispatch(Servlet servlet) throws Exception {
      servlet.service(m_req, m_res);
   }

   public HttpServletDummyRequest getRequest() {
      return m_req;
   }


   public HttpServletDummyResponse getResponse() {
      return m_res;
   }


   RequestDispatcher getDispatcher(final String path) {
      RequestDispatcher rd = null;
      String requestPath = path;

      final int queryIdx = requestPath.indexOf('?');
      if (-1 != queryIdx) {
         requestPath = requestPath.substring(0, queryIdx);
      }

      final Servlet servlet = getServletForURI(requestPath);

      if (null != servlet) {
         rd = new RequestDispatcher() {
            public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
               servlet.service(servletRequest, servletResponse);
            }

            public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
               throw new ServletException("Include not implemented");
            }
         };
      } else {
         s_log.warn("Could not get dispacher for path:" + requestPath);
         s_log.warn("Full path with query:" + path);
         java.util.Iterator iter = m_servletMapping.keySet().iterator();
         while (iter.hasNext()) {
            String url = (String) iter.next();
            s_log.warn("Have URL: " + url);
            s_log.warn("The servlet is:" + m_servletMapping.get(url));

         }


      }

      return rd;

   }

   private Servlet getServletForURI(String path) {
      Servlet servlet = (Servlet) m_servletMapping.get(path);
      if (null == servlet) {
         if (path.endsWith("/") ) {
            path = path.substring(0, path.length() - 1);
            servlet = (Servlet) m_servletMapping.get(path);

         }
      }
      return servlet;
   }

   void sendRedirect(String location) throws IOException {
      if (!location.startsWith("/")) {
         throw new IllegalArgumentException("Only supports relative redirects. Can't redirect to: " + location);
      }

      final int pathInfoStart = location.lastIndexOf('/');
      String servletPath = location.substring(0, pathInfoStart);
      final int queryStart = location.indexOf('?');
      String pathInfo;
      String queryString = null;
      if (queryStart != -1) {
         pathInfo = location.substring(pathInfoStart, queryStart);
         queryString = location.substring(queryStart);
      } else {
         pathInfo = location.substring(pathInfoStart);
      }

      ServletURL url = m_req.getServletURL();
      url.setPathInfo(pathInfo);
      url.setServletPath(servletPath);
      url.setQueryString(queryString);
      try {

         Servlet servlet = (Servlet) m_servletMapping.get(url.getServletPath());
         servlet.service(m_req, m_res);

      } catch (ServletException e) {
         throw new UncheckedWrapperException("Servlet Exception in redirect.", e);
      }
   }
}
