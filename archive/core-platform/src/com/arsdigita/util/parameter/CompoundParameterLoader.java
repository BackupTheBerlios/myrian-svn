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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @deprecated Use {@link
 * com.arsdigita.util.parameter.CompoundParameterReader} instead.
 **/

public class CompoundParameterLoader implements ParameterLoader {

    public final static String versionId = "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/CompoundParameterLoader.java#5 $ by $Author: dennis $, $DateTime: 2004/04/07 16:07:11 $";

    private final List m_loaders;

    public CompoundParameterLoader() {
        m_loaders = new ArrayList();
    }

    public void add(ParameterLoader loader) {
        m_loaders.add(loader);
    }

    public String read(final Parameter param, final ErrorList errors) {
        for (final Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            final ParameterReader reader = (ParameterReader) it.next();

            final String result = reader.read(param, errors);

            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public ParameterValue load(Parameter param) {
        for (Iterator it = m_loaders.iterator(); it.hasNext(); ) {
            ParameterLoader loader = (ParameterLoader) it.next();
            ParameterValue value = loader.load(param);
            if (value != null) { return value; }
        }

        return null;
    }

}
