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

import org.apache.commons.beanutils.converters.BooleanConverter;

/**
 * Subject to change.
 *
 * A parameter representing a Java <code>Boolean</code>.
 *
 * @see java.lang.Boolean
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/BooleanParameter.java#6 $
 */
public class BooleanParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/BooleanParameter.java#6 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/04/07 16:07:11 $";

    static {
        Converters.set(Boolean.class, new BooleanConverter());
    }

    public BooleanParameter(final String name) {
        super(name, Boolean.class);
    }

    public BooleanParameter(final String name,
                            final int multiplicity,
                            final Object defaalt) {
        super(name, multiplicity, defaalt, Boolean.class);
    }
}
