/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/servlet/HttpParameterListener.java#2 $
 */
public interface HttpParameterListener {
    static final String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/servlet/HttpParameterListener.java#2 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    void run(HttpServletRequest sreq, HttpParameterMap map);
}
