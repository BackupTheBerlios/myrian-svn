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

import java.util.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterContext.java#3 $
 */
public interface ParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterContext.java#3 $" +
        "$Author: rhs $" +
        "$DateTime: 2003/10/22 12:21:59 $";

    Parameter[] getParameters();

    Object get(Parameter param);

    Object get(Parameter param, Object dephalt);

    void set(Parameter param, Object value);

    void load(ParameterReader reader, ErrorList errors);

    void save(ParameterWriter writer);

    void validate(ErrorList errors);

}
