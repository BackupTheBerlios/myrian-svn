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
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#3 $
 */
public class SymbolicNameParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#3 $" +
        "$Author: jorris $" +
        "$DateTime: 2003/10/28 18:36:21 $";

    public SymbolicNameParameter(final String name) {
        super(name);
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final String string = (String) value;

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isJavaIdentifierPart(string.charAt(i))) {
                final ParameterError error = new ParameterError
                    (this, "The value may contain letters, digits, " +
                     "and underscores only");
                break;
            }
        }
    }
}
