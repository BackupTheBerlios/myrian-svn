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

package com.arsdigita.util.jdbc;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.net.*;
import java.util.*;
import org.apache.commons.beanutils.*;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/jdbc/JDBCURLParameter.java#1 $
 */
public class JDBCURLParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/jdbc/JDBCURLParameter.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

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

    protected void validate(final Object value, final List errors) {
        super.validate(value, errors);

        if (!s_perl.match(s_regex, (String) value)) {
            errors.add
                ("The value must start with \"jdbc:\" and take the " +
                 "form jdbc:subprotocol:subname");
        }
    }
}
