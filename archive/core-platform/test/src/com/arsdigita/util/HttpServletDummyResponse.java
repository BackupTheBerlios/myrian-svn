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

import javax.servlet.http.HttpServletResponse;
import java.lang.UnsupportedOperationException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class HttpServletDummyResponse implements HttpServletResponse {

    public static final String versionId = "$Id: //core-platform/dev/test/src/com/arsdigita/util/HttpServletDummyResponse.java#2 $ by $Author: dennis $, $DateTime: 2002/06/27 18:19:33 $";
    
  private PrintStream _out;
  private boolean     _committed;
  
  public HttpServletDummyResponse() {
    _out = System.out;
    _committed = false;
  }

  public HttpServletDummyResponse(PrintStream out) {
    _out=out;
    _committed = false;
  }

  public void addCookie(javax.servlet.http.Cookie cookie) {
    throw new UnsupportedOperationException("Method not implemented");
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
    throw new UnsupportedOperationException("Method not implemented");
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
    _committed = true;
    _out.flush();
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
    return new PrintWriter(_out);
  }

  public boolean isCommitted() {
    return _committed;
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
