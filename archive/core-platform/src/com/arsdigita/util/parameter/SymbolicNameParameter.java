/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.util.parameter;

/**
 * @deprecated This class will be supplanted by other classes in other
 * locations in the near future.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#6 $
 */
public class SymbolicNameParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/SymbolicNameParameter.java#6 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/04/07 16:07:11 $";

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
