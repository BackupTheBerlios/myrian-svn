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

package com.arsdigita.util.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpParameterListener.java#1 $
 */
public interface HttpParameterListener {
    static final String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/servlet/HttpParameterListener.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/15 01:16:50 $";

    void run(HttpServletRequest sreq, HttpParameterMap map);
}
