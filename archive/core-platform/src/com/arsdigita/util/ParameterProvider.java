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

import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public interface ParameterProvider {

    public static final String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/ParameterProvider.java#1 $ by $Author: dennis $, $DateTime: 2002/05/12 18:23:13 $";
    /**
     * Return the set of bebop ParameterModels that this provides.
     **/
    public Set getModels();
    /**
     * Return the URL parameters for this request as a set of bebop
     * ParameterData.
     **/
    public Set getParams(HttpServletRequest req);
}
