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

import com.arsdigita.util.*;
import java.util.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#3 $
 */
public class SymbolicNameParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/28 00:48:42 $";

    public SymbolicNameParameter(final String name) {
        super(name);
    }

    protected void validate(final Object value, final List errors) {
        super.validate(value, errors);

        final String string = (String) value;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isJavaIdentifierPart(string.charAt(i))) {
                errors.add("The value may contain letters, digits, " +
                           "and underscores only");
                break;
            }
        }
    }
}
