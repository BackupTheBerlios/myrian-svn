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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * A parameter representing an <code>InternetAddress</code>.
 *
 * @see javax.mail.internet.InternetAddress
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/EmailParameter.java#6 $
 */
public class EmailParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/EmailParameter.java#6 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/04/07 16:07:11 $";

    private static final Perl5Util s_perl = new Perl5Util();
    private static final String s_regex =
        "/^[^@<>\"\t ]+@[^@<>\".\t]+([.][^@<>\".\n ]+)+$/";

    public EmailParameter(final String name) {
        super(name);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        try {
            return new InternetAddress(value);
        } catch (AddressException ae) {
            errors.add(new ParameterError(this, ae));
            return null;
        }
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final InternetAddress email = (InternetAddress) value;

        if (!s_perl.match(s_regex, email.toString())) {
            final ParameterError error = new ParameterError
                (this, "The value is not a valid email address");

            errors.add(error);
        }
    }
}
