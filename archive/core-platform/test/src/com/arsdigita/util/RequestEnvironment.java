/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.dispatcher.InitialRequestContext;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.kernel.KernelRequestContext;
import com.arsdigita.kernel.security.SessionContext;
import com.arsdigita.kernel.security.UserContext;

/**
 * Class RequestEnvironment
 * 
 * @author jorris@redhat.com
 * @version $Revision $1 $ $Date: 2004/01/29 $
 */
public class RequestEnvironment {
    private HttpServletDummyRequest m_req;
    private HttpServletDummyResponse m_res;

    public RequestEnvironment() {
        setupServletContext();
        setKernelContext();
    }

    public HttpServletDummyRequest getRequest() {
        return m_req;
    }

    public HttpServletDummyResponse getResponse() {
        return m_res;
    }

    private void setupServletContext() {
        m_req = new HttpServletDummyRequest();
        m_res = new HttpServletDummyResponse();

        DispatcherHelper.setRequest(m_req);

    }

    /**
     * Sets the KernelContext in the request.
     */
    public void setKernelContext() {
        InitialRequestContext irc = new InitialRequestContext
            (m_req, new DummyServletContext());

        UserContext uc = null;
        try {
            uc = new UserContext(m_req, m_res);
        } catch (RedirectException re) {
            System.out.println(re.getMessage());
            re.printStackTrace();
        }
        SessionContext sc = uc.getSessionContext();

        KernelRequestContext krc =
            new KernelRequestContext(irc, sc, uc);
        DispatcherHelper.setRequestContext(m_req, krc);
    }

}
