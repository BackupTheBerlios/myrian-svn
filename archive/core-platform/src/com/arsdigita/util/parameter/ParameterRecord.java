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

import org.apache.log4j.Logger;

/**
 * @deprecated Use {@link com.arsdigita.runtime.AbstractConfig}
 * instead.
 */
public abstract class ParameterRecord extends AbstractParameterContext {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/ParameterRecord.java#4 $" +
        "$Author: dennis $" +
        "$DateTime: 2004/03/30 17:47:27 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    protected ParameterRecord(final String name) {
        super();
    }
}
