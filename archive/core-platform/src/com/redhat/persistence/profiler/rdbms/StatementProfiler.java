package com.redhat.persistence.profiler.rdbms;

import com.arsdigita.util.*;
import com.redhat.persistence.*;
import com.redhat.persistence.engine.rdbms.*;
import java.io.*;
import java.util.*;

public class StatementProfiler implements RDBMSProfiler {
    private boolean m_isEnabled;
    private PrintWriter m_out;
    private final ArrayList m_texts;

    public StatementProfiler() {
        m_isEnabled = false;
        m_texts = new ArrayList();
    }

    public StatementLifecycle getLifecycle(final RDBMSStatement statement) {
        if (m_isEnabled) {
            Assert.exists(m_out, PrintWriter.class);

            final String text = statement.getText();

            synchronized (m_texts) {
                if (!m_texts.contains(text)) {
                    m_texts.add(text);
                }
            }

            final int textid = m_texts.indexOf(text);

            final Lifecycle lifecycle = new Lifecycle
                (statement, textid);

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
        private long m_begin;
        private long m_elapsed;

        Lifecycle(final RDBMSStatement statement,
                  final int text) {
            m_statement = statement;
            m_text = text;
        }

        public void beginPrepare() {
            final SQLSummary summary = SQLSummary.get(m_statement.getText());

            m_out.write("\n");
            m_out.write("<statement");
            m_out.write(" text=\"" + m_text + "\"");
            m_out.write(" type=\"" + summary.getType() + "\"");
            m_out.write(">");

            final String[] tables = summary.getTables();

            for (int i = 0; i < tables.length; i++) {
                elem("table", tables[i]);
            }

            elem("query", "<![CDATA[" + m_statement.getQuery() + "]]>");

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

        public void beginSet(final int pos, final int type, final Object object) {
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

            elem("millis", Long.toString(m_elapsed));

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
            begin();

            m_out.write("<" + tag + ">");
        }

        private long end() {
            final long elapsed = now() - m_begin;

            m_elapsed += elapsed;

            return elapsed;
        }

        private void end(final String tag) {
            elem("millis", Long.toString(end()));

            m_out.write("</" + tag + ">");
        }
    }
}
