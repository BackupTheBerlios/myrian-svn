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
import com.arsdigita.util.config.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * A base class for defining configuration records.  It uses {@link
 * com.arsdigita.util.parameter parameters} to recover configuration
 * from a persistent store.
 *
 * @see com.arsdigita.util.parameter.ParameterLoader
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#13 $
 */
public abstract class ParameterRecord extends AbstractParameterContext {
    public final static String versionId =
        "$Id: //core-platform/test-packaging/src/com/arsdigita/util/parameter/ParameterRecord.java#13 $" +
        "$Author: justin $" +
        "$DateTime: 2003/10/20 01:22:54 $";

    private static final Logger s_log = Logger.getLogger
        (ParameterRecord.class);

    protected ParameterRecord(final String name) {
        super(name);
    }

    public final void load(final ParameterLoader loader) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Loading all registered params on " + this);
        }

        Assert.exists(loader, ParameterLoader.class);

        load((ParameterReader) loader);
    }
}
