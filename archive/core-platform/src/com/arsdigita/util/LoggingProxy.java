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
 * <p>This has two important consequences. Given a dynamic proxy produced by
 * {@link LoggingProxyFactory#newLoggingProxy(Object, Class, boolean)}, you can
 * </p>
 *
 * <ol>
 *  <li>get the underlying proxied object;</li>
 *  <li>adjust logging on a per instance basis.</li>
 * </ol>
 *
 * @see LoggingProxyFactory
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2003-06-17
 * @version $Revision: #1 $ $Date: 2003/06/30 $
 **/
public interface LoggingProxy extends LoggerConfigurator {

    /**
     * Returns the proxied object for which this proxy is proxying.
     **/
    Object getProxiedObject();
}

