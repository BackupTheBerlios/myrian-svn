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
package com.arsdigita.util.jdbc;

import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/jdbc/JDBCURLParameter.java#5 $
 */
public class JDBCURLParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/jdbc/JDBCURLParameter.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/04/07 16:07:11 $";

    private static final Perl5Util s_perl = new Perl5Util();
    private static final String s_regex = "/^jdbc:[^:]+:.+$/";

    public JDBCURLParameter(final String name) {
        super(name);
    }

    public JDBCURLParameter(final String name,
			    final int multiplicity,
			    final Object defaalt) {
        super(name, multiplicity, defaalt);
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final String url = (String) value;

        if (!s_perl.match(s_regex, url)) {
            final String message =
                "The value must start with \"jdbc:\" and take the " +
                "form jdbc:subprotocol:subname";

            errors.add(new ParameterError(this, message));
        }
    }
}
