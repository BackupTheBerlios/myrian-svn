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
import java.net.*;
import java.util.*;
import org.apache.commons.beanutils.*;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/URLParameter.java#3 $
 */
public class URLParameter extends StringParameter {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/URLParameter.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/08/28 00:48:42 $";

    public URLParameter(final String name) {
        super(name);
    }

    public Object unmarshal(final String value, final List errors) {
        try {
            return new URL(value);
        } catch (MalformedURLException mue) {
            errors.add(mue.getMessage());

            return null;
        }
    }
}