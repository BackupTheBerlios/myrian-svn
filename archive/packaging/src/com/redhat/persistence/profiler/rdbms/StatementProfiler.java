/*
 * Copyright (C) 2003 Red Hat Inc. All Rights Reserved.
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

package com.redhat.persistence.profiler.rdbms;

import com.arsdigita.util.*;
import com.redhat.persistence.*;
import com.redhat.persistence.metadata.*;
import com.redhat.persistence.engine.rdbms.*;
import java.io.*;
import java.util.*;

public class StatementProfiler implements RDBMSProfiler {
    private boolean m_isEnabled;
    private PrintWriter m_out;
    private final ArrayList m_texts;
    private final ArrayList m_tables;
    private final ArrayList m_types;
    private boolean m_inPhase;

    public StatementProfiler() {
        m_isEnabled = false;
        m_texts = new ArrayList();
        m_tables = new ArrayList();
        m_types = new ArrayList();
        m_inPhase = false;
    }

    public StatementLifecycle getLifecycle(final RDBMSStatement statement) {
        if (m_isEnabled) {
            Assert.exists(m_out, PrintWriter.class);

            final String text = statement.getText();

            if (!m_texts.contains(text)) {
                m_texts.add(text);
            }

            SQLSummary summary = SQLSummary.get(text);
            String[] tables = summary.getTables();
            for (int i = 0; i < tables.length; i++) {
                if (!m_tables.contains(tables[i])) {
                    m_tables.add(tables[i]);
                }
            }

            ObjectType type = null;
            Query query = statement.getQuery();
            if (query != null) {
                type = query.getSignature().getObjectType();
            } else {
                for (Iterator it = statement.getEvents().iterator();
                     it.hasNext(); ) {
                    Event ev = (Event) it.next();
                    type = Session.getObjectType(ev.getObject());
                    break;
                }
            }

            if (type != null && !m_types.contains(type)) {
                m_types.add(type);
            }

            final int textid = m_texts.indexOf(text);

            final Lifecycle lifecycle = new Lifecycle
                (statement, textid, summary, type);

            return lifecycle;
        } else {
            return null;
        }
    }

    public void start() {
        if (Assert.isEnabled()) {
            Assert.truth(m_out == null);
        }

        try {
            m_out = new PrintWriter
                (new BufferedWriter(new FileWriter(file())));
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }

        m_out.write("<?xml version=\"1.0\"?>");
        m_out.write("<profile>");

        m_isEnabled = true;
    }

    public void stop() {
        Assert.exists(m_out, PrintWriter.class);

        try {
            m_isEnabled = false;

            final Iterator texts = m_texts.iterator();

            for (int i = 0; texts.hasNext(); i++) {
                final String text = (String) texts.next();

                m_out.write("\n");
                m_out.write("<text id=\"" + i + "\">");
                m_out.write("<![CDATA[" + text + "]]>");
                m_out.write("</text>");
            }

            for (Iterator it = m_types.iterator(); it.hasNext(); ) {
                ObjectType ot = (ObjectType) it.next();
                m_out.write("\n");
                m_out.write("<type>");
                m_out.write(ot.getQualifiedName());
                m_out.write("</type>");
            }

            for (Iterator it = m_tables.iterator(); it.hasNext(); ) {
                m_out.write("\n");
                m_out.write("<table>");
                m_out.write((String) it.next());
                m_out.write("</table>");
            }

            m_out.write("\n");
            m_out.write("</profile>");
            m_out.flush();
        } finally {
            m_out.close();
            m_out = null;
        }
    }

    //
    // Private utility classes and methods
    //

    private File file() {
        try {
            return File.createTempFile("profile", ".xml");
        } catch (IOException ioe) {
            throw new UncheckedWrapperException(ioe);
        }
    }

    private void elem(final String tag, final String value) {
        m_out.write("<" + tag + ">" + value + "</" + tag + ">");
    }

    private final class Lifecycle implements StatementLifecycle {
        private final RDBMSStatement m_statement;
        private final int m_text;
        private final SQLSummary m_summary;
        private final ObjectType m_type;
        private long m_begin;

        Lifecycle(final RDBMSStatement statement,
                  final int text, final SQLSummary summary,
                  final ObjectType type) {
            m_statement = statement;
            m_text = text;
            m_summary = summary;
            m_type = type;
        }

        public void beginPrepare() {
            m_out.write("\n");
            m_out.write("<statement");
            m_out.write(" text=\"" + m_text + "\"");
            m_out.write(" type=\"" + m_summary.getType() + "\"");
            m_out.write(">");

            if (m_type != null) {
                elem("objectType", m_type.getQualifiedName());
            }

            final String[] tables = m_summary.getTables();

            for (int i = 0; i < tables.length; i++) {
                elem("table", tables[i]);
            }

            //elem("query", "<![CDATA[" + m_statement.getQuery() + "]]>");

            final Iterator events = m_statement.getEvents().iterator();

            while (events.hasNext()) {
                final Event event = (Event) events.next();

                elem("event", "<![CDATA[" + event.toString() + "]]>");
            }

            m_out.write("<lifecycle>");

            begin("prepare");
        }

        public void endPrepare() {
            end("prepare");
        }

        public void beginSet(final int pos, final int type,
                             final Object object) {
            begin("set");

            elem("pos", Integer.toString(pos));
            elem("type", Integer.toString(type));
        }

        public void endSet() {
            end("set");
        }

        public void beginExecute() {
            begin("execute");
        }

        public void endExecute(final int updateCount) {
            m_out.write("<updated>");
            m_out.write(Integer.toString(updateCount));
            m_out.write("</updated>");

            end("execute");
        }

        public void beginNext() {
            begin("next");
        }

        public void endNext(final boolean hasMore) {
            end("next");
        }

        public void beginGet(final String column) {
            begin("get");

            elem("column", column);
        }

        public void endGet(final Object result) {
            // XXX Do something with result

            end("get");
        }

        public void beginClose() {
            begin("close");
        }

        public void endClose() {
            end("close");

            m_out.write("</lifecycle>");
            m_out.write("</statement>");
        }

        //
        // Private utility classes and methods
        //

        private long now() {
            return System.currentTimeMillis();
        }

        private void begin() {
            m_begin = now();
        }

        private void begin(final String tag) {
            if (m_inPhase) {
                throw new IllegalStateException("nested begin");
            }
            m_inPhase = true;

            begin();

            m_out.write("<" + tag + ">");
        }

        private long end() {
            if (!m_inPhase) {
                throw new IllegalStateException("end called without begin");
            }
            m_inPhase = false;

            final long elapsed = now() - m_begin;

            return elapsed;
        }

        private void end(final String tag) {
            elem("millis", Long.toString(end()));

            m_out.write("</" + tag + ">");
        }
    }
}
