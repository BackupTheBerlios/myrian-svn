/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/BooleanParameter.java#5 $
 */
public class BooleanParameter extends AbstractParameter {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/BooleanParameter.java#5 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

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
