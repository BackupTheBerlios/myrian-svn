/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.util;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.PrintStream;
import java.io.PrintWriter;

public class HttpServletDummyResponse implements HttpServletResponse {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/HttpServletDummyResponse.java#8 $ by $Author: dennis $, $DateTime: 2004/03/30 17:47:27 $";

    private PrintStream m_out;
    private boolean     m_committed;
    private TestServletContainer m_container;
   private Cookie m_cookie;

    public HttpServletDummyResponse() {
        m_out = System.out;
        m_committed = false;
    }

    public HttpServletDummyResponse(PrintStream out) {
        m_out=out;
        m_committed = false;
    }

    void setContainer(TestServletContainer container) {
        m_container = container;
    }


    public void addCookie(javax.servlet.http.Cookie cookie) {
      m_cookie = cookie;
    }

    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String encodeRedirectURL(String url) {
        return url;
    }

    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    public String encodeURL(String url){
        return url;
    }

    public String encodeUrl(String url){
        return encodeURL(url);
    }

    public void sendError(int sc) throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void sendError(int sc, String msg) throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void sendRedirect(String location) throws java.io.IOException{
        m_container.sendRedirect(location);
    }

    public void setDateHeader(String name, long date){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setHeader(String name, String value){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setIntHeader(String name, int value){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setStatus(int sc){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setStatus(int sc, String sm){
        throw new UnsupportedOperationException("Method not implemented");
    }


    /* Methods from SevletResponse */
    public void flushBuffer() throws java.io.IOException{
        m_committed = true;
        m_out.flush();
    }

    public void resetBuffer() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public int getBufferSize(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String getCharacterEncoding(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public java.util.Locale getLocale(){
        throw new UnsupportedOperationException("Method not implemented");
    }

    public javax.servlet.ServletOutputStream getOutputStream() throws java.io.IOException{
        throw new UnsupportedOperationException("Method not implemented");
    }

    public PrintWriter getWriter() throws java.io.IOException {
        return new PrintWriter(m_out);
    }

    public boolean isCommitted() {
        return m_committed;
    }

    public void reset() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setBufferSize(int size) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setContentLength(int len) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setContentType(String type) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void setLocale(java.util.Locale loc) {
        throw new UnsupportedOperationException("Method not implemented");
    }

}
