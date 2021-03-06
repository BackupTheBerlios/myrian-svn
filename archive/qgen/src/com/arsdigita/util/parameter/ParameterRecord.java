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

import org.apache.log4j.Logger;

/**
 * @deprecated Use {@link com.arsdigita.runtime.AbstractConfig}
 * instead.
 */
public abstract class ParameterRecord extends AbstractParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-qgen/src/com/arsdigita/util/parameter/ParameterRecord.java#1 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/12/10 16:59:20 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    protected ParameterRecord(final String name) {
        super();
    }
}
