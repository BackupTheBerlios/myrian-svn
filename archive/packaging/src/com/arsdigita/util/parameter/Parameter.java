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
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Revision: #1 $ $Date: 2003/08/26 $
 */
public interface Parameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/Parameter.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/26 11:56:51 $";

    public boolean isRequired();

    public String getName();

    public Object getValue(ParameterStore store);

    public List validate(ParameterStore store);
}
