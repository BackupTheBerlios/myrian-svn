/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.arsdigita.util;

/**
 * Dynamic proxies produced by the {@link LoggingProxyFactory} implement
 * this interface.
 * 
 * @see LoggingProxyFactory
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-06-17
 * @version $Revision: #1 $ $Date: 2003/06/30 $
 **/
public interface LoggerConfigurator {

     void setLogger(String logger);

     void setLevel(String level);

     void enableStackTraces();

     // TODO: add API for specifying match patterns so that calls can be logged
     // selectively, depending on whether they match or fail to match a
     // particular pattern.  The pattern format should probably allow the
     // following syntax:
     //   *  "fooBar" - match any method that contains "fooBar" as a substring
     //   *  "^fooBaz" - match any method that starts with "fooBaz"
     //   *  "fooBaz$" - match any method that ends with "fooBaz"
     //   *  "^fooBaz$" - match the "fooBaz" method exactly.
}
