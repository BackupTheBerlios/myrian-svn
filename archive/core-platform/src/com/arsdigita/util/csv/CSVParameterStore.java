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

package com.arsdigita.util.csv;

import com.arsdigita.util.*;
import com.arsdigita.util.parameter.*;
import java.io.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/csv/CSVParameterStore.java#1 $
 */
public final class CSVParameterStore implements ParameterStore {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/csv/CSVParameterStore.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/09 14:53:22 $";

    private final BufferedReader m_reader;
    private final Parameter[] m_params;
    private final HashMap m_line;

    public CSVParameterStore(final Reader reader, final Parameter[] params) {
        m_reader = new BufferedReader(reader);
        m_params = params;
        m_line = new HashMap(params.length);
    }

    public final String read(final Parameter param) {
        return (String) m_line.get(param);
    }

    public final void write(final Parameter param, final String value) {
        // Nada
    }

    public final boolean next() {
        try {
            return internalNext();
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    private boolean internalNext() throws IOException {
        final String line = m_reader.readLine();

        if (line == null) {
            return false;
        } else {
            final String[] elems = parseLine(line);

            for (int i = 0; i < m_params.length; i++) {
                m_line.put(m_params[i], elems[i]);
            }

            return true;
        }
    }

    private String[] parseLine(final String line) {
        // XXX currently looking for an unencumbered implementation.

        return null;
    }
}
