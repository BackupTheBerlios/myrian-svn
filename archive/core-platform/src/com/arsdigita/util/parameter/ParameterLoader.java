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

package com.arsdigita.util.parameter;



/**
 * @deprecated Use {@link
 * com.arsdigita.util.parameter.ParameterReader} instead.
 */
public interface ParameterLoader extends ParameterReader {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterLoader.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/28 18:36:21 $";

    ParameterValue load(Parameter param);
}
