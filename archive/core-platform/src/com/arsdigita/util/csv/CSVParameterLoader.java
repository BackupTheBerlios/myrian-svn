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
 * @version $Id: //core-platform/dev/src/com/arsdigita/util/csv/CSVParameterLoader.java#1 $
 */
public final class CSVParameterLoader implements ParameterLoader {
    public final static String versionId =
        "$Id: //core-platform/dev/src/com/arsdigita/util/csv/CSVParameterLoader.java#1 $" +
        "$Author: justin $" +
        "$DateTime: 2003/09/26 15:31:04 $";

    private final LineNumberReader m_reader;
    private final Parameter[] m_params;
    private final HashMap m_line;

    public CSVParameterLoader(final Reader reader, final Parameter[] params) {
        m_reader = new LineNumberReader(reader);
        m_params = params;
        m_line = new HashMap(params.length);
    }

    public final ParameterValue load(final Parameter param) {
        final ParameterValue value = new ParameterValue();

        value.setString((String) m_line.get(param));

        param.unmarshal(value);
        param.check(value);

        return value;
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
                if (i < elems.length) {
                    m_line.put(m_params[i], elems[i]);
                } else {
                    m_line.put(m_params[i], null);
                }
            }

            return true;
        }
    }

    private static final char ESCAPE = '\\';
    private static final char QUOTE = '"';
    private static final char SEPARATOR = ',';

    private char escape(char c) {
        switch (c) {
        case 'n':
            return '\n';
        case 't':
            return '\t';
        case 'r':
            return '\r';
        default:
            return c;
        }
    }

    private String[] parseLine(final String line) {
        int length = line.length();

        // Check here if the last character is an escape character so
        // that we don't need to check in the main loop.
        if (line.charAt(length - 1) == ESCAPE) {
            throw new IllegalArgumentException
                (m_reader.getLineNumber() +
                 ": last character is an escape character\n" + line);
        }

        // The set of parsed fields.
        List result = new ArrayList();

        // The characters between seperators.
        StringBuffer buf = new StringBuffer(length);
        // Marks the begining of the field relative to buf, -1
        // indicates the beginning of buf.
        int begin = -1;
        // Marks the end of the field relative to buf.
        int end = 0;

        // Indicates whether or not we're in a quoted string.
        boolean quote = false;

        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);
            if (quote) {
                switch (c) {
                case QUOTE:
                    quote = false;
                    break;
                case ESCAPE:
                    buf.append(escape(line.charAt(++i)));
                    break;
                default:
                    buf.append(c);
                    break;
                }

                end = buf.length();
            } else {
                switch (c) {
                case SEPARATOR:
                    result.add(field(buf, begin, end));
                    buf = new StringBuffer(length);
                    begin = -1;
                    end = 0;
                    break;
                case ESCAPE:
                    if (begin < 0) { begin = buf.length(); }
                    buf.append(escape(line.charAt(++i)));
                    end = buf.length();
                    break;
                case QUOTE:
                    if (begin < 0) { begin = buf.length(); }
                    quote = true;
                    end = buf.length();
                    break;
                default:
                    if (begin < 0 &&
                        !Character.isWhitespace(c)) {
                        begin = buf.length();
                    }
                    buf.append(c);
                    if (!Character.isWhitespace(c)) { end = buf.length(); }
                    break;
                }
            }
        }

        if (quote) {
            throw new IllegalArgumentException
                (m_reader.getLineNumber() + ": unterminated string\n" + line);
        } else {
            result.add(field(buf, begin, end));
        }

        String[] fields = new String[result.size()];
        result.toArray(fields);
        return fields;
    }

    private String field(StringBuffer field, int begin, int end) {
        if (begin < 0) {
            return field.substring(0, end);
        } else {
            return field.substring(begin, end);
        }
    }

}
