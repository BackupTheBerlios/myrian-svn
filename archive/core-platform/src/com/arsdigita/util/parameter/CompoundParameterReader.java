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

import com.arsdigita.util.Assert;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * Aggregates a set of <code>ParameterReaders</code> so they may be
 * treated as one.
 *
 * @see ParameterReader
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/parameter/CompoundParameterReader.java#3 $
 */
public class CompoundParameterReader implements ParameterReader {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/parameter/CompoundParameterReader.java#3 $" +
        "$Author: justin $" +
        "$DateTime: 2003/11/10 12:29:19 $";

    private static final Logger s_log = Logger.getLogger
        (CompoundParameterReader.class);

    private final List m_readers;

    /**
     * Constructs a new compound parameter reader.
     */
    public CompoundParameterReader() {
        m_readers = new ArrayList();
    }

    /**
     * Adds <code>reader</code> to the set of component readers.
     *
     * @param reader The <code>ParameterReader</code> being added; it
     * cannot be null
     */
    public void add(final ParameterReader reader) {
        Assert.exists(reader, ParameterReader.class);

        m_readers.add(reader);
    }

    /**
     * @see ParameterReader#read(Parameter,ErrorList)
     */
    public String read(final Parameter param, final ErrorList errors) {
        for (final Iterator it = m_readers.iterator(); it.hasNext(); ) {
            final ParameterReader reader = (ParameterReader) it.next();

            final String result = reader.read(param, errors);

            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
